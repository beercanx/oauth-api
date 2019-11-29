package com.sbgcore.oauth.api.wellknown

class WellKnown {

    fun getOpenIdConfiguration(): OpenIdConfiguration {

        // TODO - Something like load from config?
        return OpenIdConfiguration(
            issuer = "https://auth.test.skybetservices.com.skybet.net",
            authorization_endpoint = "https://auth.test.skybetservices.com.skybet.net/openid/v1/authorize"
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
                Product(id = "fantasyfootball"),
                Product(id = "itv7")
            )
        )
    }
}