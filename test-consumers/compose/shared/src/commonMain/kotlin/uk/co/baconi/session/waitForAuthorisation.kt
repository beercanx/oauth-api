package uk.co.baconi.session

import kotlinx.coroutines.CoroutineScope
import uk.co.baconi.session.oauth.AuthorisationCode

expect suspend fun waitForAuthorisation(coroutineScope: CoroutineScope): AuthorisationCode