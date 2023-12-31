package uk.co.baconi.session

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

actual class SessionManagerEx actual constructor(sessionService: SessionService) : SessionManagerShared() {

    actual val isAuthorising: Flow<Boolean>
        get() = TODO("Not yet implemented")

    actual fun startLogin() {
    }

    actual fun cancelLogin() {
    }
}