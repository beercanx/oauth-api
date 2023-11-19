package uk.co.baconi.session

import kotlinx.coroutines.CoroutineScope
import uk.co.baconi.session.oauth.AuthorisationCode

actual suspend fun waitForAuthorisation(coroutineScope: CoroutineScope): AuthorisationCode {
    // TODO("Not yet implemented")
    return AuthorisationCode("TODO")
}