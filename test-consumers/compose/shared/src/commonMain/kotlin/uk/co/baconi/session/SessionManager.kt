package uk.co.baconi.session

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.co.baconi.coroutines.Dispatchers
import uk.co.baconi.session.oauth.State

class SessionManager {

    private val sessionService = SessionService()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _session = MutableStateFlow<Session?>(null)
    val session = _session.asStateFlow()

    fun startLoginFlow() {
        // TODO - Place a lock to prevent multiple?
        coroutineScope.launch {
            try {
                val state = State(generateUUID())
                val verifier = sessionService.createVerifier()
                val challenge = sessionService.createChallenge(verifier)
                val authoriseUrl = sessionService.authoriseUrl(state, challenge)

                launchAuthorise(authoriseUrl)

                val code = waitForAuthorisation(coroutineScope)

                val session = sessionService.authorisationCodeGrant(code, verifier)
                if(session.state == state) _session.value = session
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }
}
