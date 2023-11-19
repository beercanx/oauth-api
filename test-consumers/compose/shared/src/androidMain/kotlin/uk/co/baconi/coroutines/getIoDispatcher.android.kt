package uk.co.baconi.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual fun getIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
