package uk.co.baconi.session

import io.ktor.http.*

actual fun getRedirectUri(): Url = URLBuilder("http://localhost:8081").build()
