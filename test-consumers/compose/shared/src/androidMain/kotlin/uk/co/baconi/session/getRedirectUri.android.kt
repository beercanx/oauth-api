package uk.co.baconi.session

import io.ktor.http.*

actual fun getRedirectUri(): Url = URLBuilder("uk.co.baconi.test-comsumer-compose://callback").build()
