package uk.co.baconi.session

import io.ktor.http.*

expect suspend fun launchAuthorise(url: Url)