package com.sbgcore.oauth.api

import com.sbgcore.oauth.api.authentication.ClientSecret
import com.sbgcore.oauth.api.authentication.ConfidentialClient
import com.sbgcore.oauth.api.ktor.basic
import com.sbgcore.oauth.api.customer.openbet.OpenBetMatchService
import com.sbgcore.oauth.api.openid.exchange.flows.assertion.AssertionRedemptionFlow
import com.sbgcore.oauth.api.openid.exchange.flows.authorization.AuthorizationCodeFlow
import com.sbgcore.oauth.api.openid.exchange.flows.password.PasswordFlow
import com.sbgcore.oauth.api.openid.exchange.flows.refresh.RefreshFlow
import com.sbgcore.oauth.api.openid.exchange.tokens.AccessTokenService
import com.sbgcore.oauth.api.openid.introspection.IntrospectionService
import com.sbgcore.oauth.api.openid.openIdRoutes
import com.sbgcore.oauth.api.storage.nitrite.NitriteAccessTokenRepository
import com.sbgcore.oauth.api.storage.nitrite.NitriteClientSecretRepository
import com.sbgcore.oauth.api.wellknown.wellKnownRoutes
import com.skybettingandgaming.oxi.client.ktor.XmlFeature
import com.skybettingandgaming.oxi.client.ktor.serializer.OxiSerializer
import com.skybettingandgaming.oxi.dto.ReqClientAuth
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.serialization.*
import org.bouncycastle.crypto.generators.OpenBSDBCrypt

@Suppress("unused") // Inform the IDE that we are actually using this
@KtorExperimentalLocationsAPI
fun Application.main() {

    install(Locations)
    install(AutoHeadResponse)
    install(DataConversion)

    install(HSTS) {
        includeSubDomains = true
    }

    install(ContentNegotiation) {
        json()
    }

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
        options { CachingOptions(CacheControl.NoStore(null)) }
        // no-cache
        options { CachingOptions(CacheControl.NoCache(null)) }
        // must-revalidate, proxy-revalidate, max-age=0
        options {
            CachingOptions(
                CacheControl.MaxAge(
                    maxAgeSeconds = 0,
                    mustRevalidate = true,
                    proxyRevalidate = true
                )
            )
        }
    }

    val config = ConfigFactory.load().getConfig("com.sbgcore.oauth.api")

    val clientSecretRepository = NitriteClientSecretRepository()

    install(Authentication) {
        basic<ConfidentialClient> {
            realm = "skybettingandgaming"
            validate { (clientId, clientSecret) ->
                // TODO - Refactor into testable unit
                clientSecretRepository
                    .findAllByClientId(clientId)
                    .filter { secret ->
                        OpenBSDBCrypt.checkPassword(secret.secret, clientSecret.toCharArray())
                    }
                    .map(ClientSecret::clientId)
                    .map(::ConfidentialClient)
                    .firstOrNull()
            }
        }
    }

    //
    // Dependencies for injection
    //

    val oxiHttpClient = HttpClient(OkHttp) {
        install(XmlFeature) {
            serializer = OxiSerializer {
                ReqClientAuth
                    .builder()
                    .user(config.getString("oxi.user"))
                    .password(config.getString("oxi.password"))
                    .build()
            }
        }
        defaultRequest {
            url(config.getString("oxi.url"))
            contentType(ContentType.Application.Xml)
        }
        engine {
            config {
                connectTimeout(config.getDuration("oxi.timeouts.connect"))
                readTimeout(config.getDuration("oxi.timeouts.read"))
                writeTimeout(config.getDuration("oxi.timeouts.write"))
                callTimeout(config.getDuration("oxi.timeouts.call"))
            }
        }
    }

    // Repositories
    val accessTokenRepository = NitriteAccessTokenRepository()

    // Services
    val loginService = OpenBetMatchService(oxiHttpClient)
    val accessTokenService = AccessTokenService(accessTokenRepository)

    // Flows
    val passwordFlow = PasswordFlow(loginService, accessTokenService)
    val refreshFlow = RefreshFlow()
    val authorizationCodeFlow = AuthorizationCodeFlow()
    val assertionRedemptionFlow = AssertionRedemptionFlow()
    val introspectionService = IntrospectionService()

    //
    // Setup the well known routes
    //
    wellKnownRoutes()

    //
    // Setup the OpenID connect routes
    //
    openIdRoutes(passwordFlow, refreshFlow, authorizationCodeFlow, assertionRedemptionFlow, introspectionService)

    // TODO - Account routes
    // TODO - Product transfer routes
}