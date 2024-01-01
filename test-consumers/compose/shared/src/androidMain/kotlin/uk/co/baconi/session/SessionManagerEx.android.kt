package uk.co.baconi.session

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import uk.co.baconi.session.oauth.AuthorisationCode
import uk.co.baconi.session.oauth.CodeVerifier
import uk.co.baconi.session.oauth.State

actual class SessionManagerEx actual constructor(val sessionService: SessionService) : SessionManagerShared() {

    companion object {
        private const val TAG = "SessionManagerEx"
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val callback = MutableStateFlow<Job?>(null)
    actual val isAuthorising = callback.map { it?.isActive == true } // TODO - Possibly pointless on Android

    actual suspend fun startLogin()  {
        // TODO - Replace with flavour variations instead of shared code.
    }

    fun startLogin(context: Context, state: State, verifier: CodeVerifier) {
        val challenge = runBlocking(Dispatchers.IO) { sessionService.createChallenge(verifier) }
        val authoriseUrl = sessionService.authoriseUrl(state, challenge)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authoriseUrl.toString()))
        context.startActivity(intent)
    }

    actual fun cancelLogin() {
        // TODO - Replace with flavour variations instead of shared code.
    }

    fun handleCallback(callback: Url, state: State, verifier: CodeVerifier) {
        this.callback.value = coroutineScope.launch  {
            try {
                val code = callback.parameters["code"] ?: throw RuntimeException("Received a response with no code")
                val authorisationCode = AuthorisationCode(code)
                val session = sessionService.authorisationCodeGrant(authorisationCode, verifier)
                if(session.state == state) mutableSession.value = session // TODO - Replace mutableSession with suitable Android solution
            } catch (exception: Exception) {
                Log.e(TAG, "handling callback failed", exception)
            }
        }.also { job ->
            job.invokeOnCompletion {
                this.callback.value = null
            }
        }
    }
}