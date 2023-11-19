package uk.co.baconi.coroutines

import kotlinx.coroutines.CoroutineDispatcher

expect fun getIoDispatcher(): CoroutineDispatcher
