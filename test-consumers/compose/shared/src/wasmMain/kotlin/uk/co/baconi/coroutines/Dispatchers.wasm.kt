package uk.co.baconi.coroutines

import kotlinx.coroutines.CoroutineDispatcher

actual object Dispatchers {
    actual val Default: CoroutineDispatcher = kotlinx.coroutines.Dispatchers.Default
    actual val IO: CoroutineDispatcher = kotlinx.coroutines.Dispatchers.Default
}