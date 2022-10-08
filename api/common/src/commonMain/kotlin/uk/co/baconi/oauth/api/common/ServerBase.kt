package uk.co.baconi.oauth.api.common

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.cachingheaders.CachingHeaders
import io.ktor.server.plugins.dataconversion.DataConversion
import io.ktor.server.plugins.hsts.HSTS
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.resources.Resources

object ServerBase {

    fun Application.module() {

        install(Resources)
        install(AutoHeadResponse)
        install(DataConversion)

        install(HSTS) {
            includeSubDomains = true
        }

        install(ContentNegotiation) {
            json()
        }

//        install(Compression) {
//            gzip {
//                priority = 1.0
//            }
//            deflate {
//                priority = 10.0
//                minimumSize(1024) // condition
//            }
//        }

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
    }
}