package uk.co.baconi.oauth.api.common

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.dataconversion.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.plugins.hsts.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import uk.co.baconi.oauth.api.common.authentication.AuthenticateSession
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedSession
import kotlin.collections.forEach
import kotlin.time.Duration.Companion.minutes

object CommonModule {

    @OptIn(ExperimentalSerializationApi::class)
    fun Application.common() {

        log.info("Registering the CommonModule.common() module")

        install(AutoHeadResponse)
        install(DataConversion) // TODO - Do we even need this?

        // TODO - Include HTTPS redirect - enabled via config - local setup to off.
        install(HSTS) {
            includeSubDomains = true
        }

        install(ContentNegotiation) {
            json(Json(DefaultJson) {
                explicitNulls = false
            })
        }

        // Removing this could enable a Native first server (a long side replacing the DB)
        install(Compression) {
            gzip {
                priority = 1.0
            }
            deflate {
                priority = 10.0
                minimumSize(1024) // condition
            }
        }

        // Disable caching via headers on all requests
        install(CachingHeaders) {
            // no-store
            options { _, _ -> CachingOptions(CacheControl.NoStore(null)) }
            // no-cache
            options { _, _ -> CachingOptions(CacheControl.NoCache(null)) }
            // max-age=0, must-revalidate, proxy-revalidate
            options { _, _ -> CachingOptions(CacheControl.MaxAge(0, mustRevalidate = true, proxyRevalidate = true)) }
        }

        // Enable `call.receive` to work twice without getting an exception
        install(DoubleReceive)

        // Enable CORS to enable multiple ports - TODO - Remove once we've sorted common domain setup
        install(CORS) {
            (0..9).forEach {
                this@common.log.info("CORS allowHost localhost:808${it}")
                allowHost("localhost:808${it}")
            }
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Authorization) // TODO - Remove once we stop doing Password Grant in the browser
            allowCredentials = true
        }

        install(Sessions) {
            cookie<AuthenticateSession>("authenticate") {
                cookie.path = "/"
                cookie.maxAge = 5.minutes
                transform(
                    SessionTransportTransformerEncrypt( // TODO - Extract into config
                        hex("901c7e7ad029ad1ecfab8020d0005ee0"), // 128-bit
                        hex("8d712329a4d2cbab") // 64-bit
                    )
                )
            }
            cookie<AuthenticatedSession>("authenticated") {
                cookie.path = "/"
                cookie.maxAge = 30.minutes
                transform(
                    SessionTransportTransformerEncrypt( // TODO - Extract into config
                        hex("854dd2c698be10ea0ae7cd0337b23721"), // 128-bit
                        hex("62faa3a4a55c3108") // 64-bit
                    )
                )
            }
        }

        // TODO - Call ID
        // TODO - Metrics
    }
}