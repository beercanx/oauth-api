package uk.co.baconi.session

import kotlinx.coroutines.flow.*

open class SessionManagerShared {

    protected val mutableSession = MutableStateFlow<Session?>(null)

    /**
     * Contains the current session, if there is one at all.
     */
    val session: StateFlow<Session?> = mutableSession.asStateFlow()

}

expect class SessionManagerEx(sessionService: SessionService = SessionService()) : SessionManagerShared {

    /**
     * Indicates if an authorise attempt has been started.
     */
    val isAuthorising: Flow<Boolean>

    /**
     * Contains the current session, if there is one at all.
     */
    //val session: StateFlow<Session?>

    /**
     * Starts the authorisation flow.
     */

    fun startLogin()

    /**
     * Cancels any attempt at an authorisation flow.
     */
    fun cancelLogin()

    /**
     * Triggers the logout and invalidation of the current session
     */
    // fun logout() // TODO - Support logout
}
