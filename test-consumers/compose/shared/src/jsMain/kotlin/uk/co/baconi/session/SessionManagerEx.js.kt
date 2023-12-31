package uk.co.baconi.session

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

actual class SessionManagerEx actual constructor(sessionService: SessionService) : SessionManagerShared() {

    private val callback = MutableStateFlow<Job?>(null)
    actual val isAuthorising = callback.map { it?.isActive == true }

    actual fun startLogin() {
    }

    actual fun cancelLogin() {
    }
}