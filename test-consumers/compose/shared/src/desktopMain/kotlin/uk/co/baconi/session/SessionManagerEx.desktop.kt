package uk.co.baconi.session

import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import uk.co.baconi.session.oauth.AuthorisationCode
import java.awt.Desktop
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

actual class SessionManagerEx actual constructor(private val sessionService: SessionService) : SessionManagerShared() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine. Configuration>? = null

    actual suspend fun startLogin() {
        try {
            val state = sessionService.createState()
            val verifier = sessionService.createVerifier()
            val challenge = sessionService.createChallenge(verifier)
            val authoriseUrl = sessionService.authoriseUrl(state, challenge)

            launchAuthorise(authoriseUrl)

            val code = waitForAuthorisation()

            val session = sessionService.authorisationCodeGrant(code, verifier)
            if (session.state == state) mutableSession.value = session
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
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
                    get("/callback") {
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