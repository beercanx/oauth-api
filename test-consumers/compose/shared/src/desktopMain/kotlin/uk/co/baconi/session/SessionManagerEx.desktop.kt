package uk.co.baconi.session

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import uk.co.baconi.session.oauth.AuthorisationCode
import uk.co.baconi.session.oauth.State
import java.awt.Desktop
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

actual class SessionManagerEx actual constructor(private val sessionService: SessionService) : SessionManagerShared() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var server: NettyApplicationEngine? = null
    private val callback = MutableStateFlow<Job?>(null)
    actual val isAuthorising = callback.map { it?.isActive == true }

    actual suspend fun startLogin() {

        if(callback.value != null) return

        callback.value = coroutineScope.launch {
            try {
                val state = sessionService.createState()
                val verifier = sessionService.createVerifier()
                val challenge = sessionService.createChallenge(verifier)
                val authoriseUrl = sessionService.authoriseUrl(state, challenge)

                launchAuthorise(authoriseUrl)

                val code = waitForAuthorisation()

                val session = sessionService.authorisationCodeGrant(code, verifier)
                if(session.state == state) mutableSession.value = session
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }.also { job ->
            job.invokeOnCompletion {
                callback.value = null
            }
        }
    }

    actual fun cancelLogin() {
        callback.value?.cancel()
        callback.value = null
        server?.stop(0, 0)
        server = null
    }

    private suspend fun launchAuthorise(url: Url) {
        withContext(Dispatchers.IO) {
            Desktop.getDesktop().browse(url.toURI())
        }
    }

    private suspend fun waitForAuthorisation(): AuthorisationCode {

        val code = suspendCancellableCoroutine { continuation ->
            server = embeddedServer(Netty, port = 8180) {
                routing {
                    get ("/callback") {
                        val code = call.parameters["code"] ?: throw RuntimeException("Received a response with no code")
                        call.respondText("OK")
                        continuation.resume(AuthorisationCode(code))
                    }
                }
            }.start(wait = false)
        }

        coroutineScope.launch {
            server?.stop(1, 5, TimeUnit.SECONDS)
            server = null
        }

        return code
    }
}