package com.sbgcore.oauth.api.wellknown

import com.sbgcore.oauth.api.jwk.JsonWebKey
import com.sbgcore.oauth.api.jwk.JsonWebKeySet

class WellKnown {

    fun getOpenIdConfiguration(): OpenIdConfiguration {

        // TODO - Something like load from config?
        return OpenIdConfiguration(
            issuer = "https://auth.localhost.me",
            authorization_endpoint = "https://auth.localhost.me/oauth/v1/authorize"
        )
    }

    fun getJsonWebKeySet(): JsonWebKeySet {

        // TODO - Something like load from config?
        return JsonWebKeySet(
            keys = setOf(
                JsonWebKey(
                    kty = "RSA",
                    use = "sig",
                    alg = "RS256"
                )
            )
        )
    }

    fun getProductConfiguration(): ProductConfiguration {

        // TODO - Something like load from config?
        return ProductConfiguration(
            products = listOf(
                Product(id = "consumer-x"),
                Product(id = "consumer-y"),
                Product(id = "consumer-z"),
            )
        )
    }
}