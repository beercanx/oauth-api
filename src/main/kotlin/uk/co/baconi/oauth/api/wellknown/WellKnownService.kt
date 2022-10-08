package uk.co.baconi.oauth.api.wellknown

import uk.co.baconi.oauth.api.jwk.JsonWebKey
import uk.co.baconi.oauth.api.jwk.JsonWebKeySet

class WellKnownService {

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