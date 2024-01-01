package uk.co.baconi.session

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import uk.co.baconi.preferences.get
import uk.co.baconi.preferences.oAuthDataStore
import uk.co.baconi.preferences.set
import uk.co.baconi.session.oauth.AuthorisationCode
import uk.co.baconi.session.oauth.CodeVerifier
import uk.co.baconi.session.oauth.State

actual class SessionManagerEx actual constructor(private val sessionService: SessionService) : SessionManagerShared() {

    companion object {
        private const val TAG = "SessionManagerEx"
        private val STATE = stringPreferencesKey("state")
        private val CODE_VERIFIER = stringPreferencesKey("code_verifier")

        private val coroutineScope = CoroutineScope(Dispatchers.IO)
    }

    private lateinit var context: Context

    constructor(context: Context): this() {
        this.context = context
    }

    private suspend fun getState() = context.oAuthDataStore.get(STATE).map(::State).firstOrNull()
    private suspend fun setState(state: State) = context.oAuthDataStore.set(STATE, state.value)

    private suspend fun getVerifier() = context.oAuthDataStore.get(CODE_VERIFIER).map(::CodeVerifier).firstOrNull()
    private suspend fun setVerifier(verifier: CodeVerifier) = context.oAuthDataStore.set(CODE_VERIFIER, verifier.value)

    actual suspend fun startLogin()  {
        val state = sessionService.createState().also { setState(it) }
        val verifier = sessionService.createVerifier().also { setVerifier(it) }
        val challenge = sessionService.createChallenge(verifier)
        val authoriseUrl = sessionService.authoriseUrl(state, challenge)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authoriseUrl.toString()))
        context.startActivity(intent)
    }

    fun handleCallback(callback: Url) {
        coroutineScope.launch  {
            try {
                val state = checkNotNull(getState()) { "Stored State was unexpectedly null" }
                val verifier = checkNotNull(getVerifier()) { "Stored CodeVerifier was unexpectedly null" }
                val code = checkNotNull(callback.parameters["code"]) { "Received a response with no code" }
                val authorisationCode = AuthorisationCode(code)
                val session = sessionService.authorisationCodeGrant(authorisationCode, verifier)
                if(session.state == state) mutableSession.value = session // TODO - Replace mutableSession with suitable Android solution
            } catch (exception: Exception) {
                Log.e(TAG, "handling callback failed", exception)
            }
        }
    }
}