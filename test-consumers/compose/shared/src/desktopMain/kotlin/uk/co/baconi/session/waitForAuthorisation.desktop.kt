package uk.co.baconi.session

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import uk.co.baconi.session.oauth.AuthorisationCode
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

actual suspend fun waitForAuthorisation(coroutineScope: CoroutineScope): AuthorisationCode {

    lateinit var server: NettyApplicationEngine

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
        server.stop(1, 5, TimeUnit.SECONDS)
    }

    return code
}