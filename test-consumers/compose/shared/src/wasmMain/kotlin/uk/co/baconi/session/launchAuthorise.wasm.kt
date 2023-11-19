package uk.co.baconi.session

import io.ktor.http.*

actual suspend fun launchAuthorise(url: Url) {
    println("launchAuthorise: $url")
    // TODO - Do wasm stuff
}