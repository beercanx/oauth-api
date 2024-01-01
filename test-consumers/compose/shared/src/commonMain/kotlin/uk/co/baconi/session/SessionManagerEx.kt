package uk.co.baconi.session

import kotlinx.coroutines.flow.*

open class SessionManagerShared {

    protected val mutableSession = MutableStateFlow<Session?>(null)

    /**
     * Contains the current session, if there is one at all.
     */
    val session: StateFlow<Session?> = mutableSession.asStateFlow()

}

// TODO - Replace with function and interface pattern for stable multiplatform support.
expect class SessionManagerEx(sessionService: SessionService = SessionService()) : SessionManagerShared {

    /**
     * Starts the authorisation flow.
     */
    suspend fun startLogin()

    /**
     * Cancels any attempt at an authorisation flow.
     */
    // fun cancelLogin() // TODO - Is support required?

    /**
     * Triggers the logout and invalidation of the current session
     */
    // fun logout() // TODO - Support logout
}
