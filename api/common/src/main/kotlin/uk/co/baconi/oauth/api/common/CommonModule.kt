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

object CommonModule {

    fun Application.common() {

        log.info("Registering the CommonModule.common() module")

        install(AutoHeadResponse)
        install(DataConversion) // TODO - Do we even need this?

        // TODO - Include HTTPS redirect - enabled via config - local setup to off.
        install(HSTS) {
            includeSubDomains = true
        }

        install(ContentNegotiation) {
            json()
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
            // must-revalidate, proxy-revalidate, max-age=0
            options { _, _ -> CachingOptions(
                CacheControl.MaxAge(
                    maxAgeSeconds = 0,
                    mustRevalidate = true,
                    proxyRevalidate = true
                )
            )
            }
        }

        // Enable `call.receive` to work twice without getting an exception
        install(DoubleReceive)

        // Enable CORS to enable multiple ports - TODO - Remove once we've sorted common domain setup
        install(CORS) {
            (0..8).forEach {
                this@common.log.info("CORS allowHost localhost:808${it}")
                allowHost("localhost:808${it}")
            }
            allowHeader(HttpHeaders.ContentType)
            allowCredentials = true
        }

        // TODO - Call ID
        // TODO - Metrics
    }
}