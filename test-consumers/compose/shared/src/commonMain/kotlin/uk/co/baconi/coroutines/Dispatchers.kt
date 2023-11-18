package uk.co.baconi.coroutines

import kotlinx.coroutines.CoroutineDispatcher

expect object Dispatchers {
    val Default: CoroutineDispatcher
    val IO: CoroutineDispatcher
 }