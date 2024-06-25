package uk.co.baconi.oauth.api.gatling.endpoints

import io.gatling.javaapi.core.CheckBuilder
import io.gatling.javaapi.core.CoreDsl.jsonPath
import io.gatling.javaapi.core.Session
import io.gatling.javaapi.http.HttpDsl.*
import uk.co.baconi.oauth.api.gatling.endpoints.AuthorisationEndpoint.Expressions.authorisationCode
import uk.co.baconi.oauth.api.gatling.endpoints.TokenEndpoint.Checks.hasAccessTokenAndSave
import uk.co.baconi.oauth.api.gatling.endpoints.TokenEndpoint.Checks.hasBearerTokenType
import uk.co.baconi.oauth.api.gatling.endpoints.TokenEndpoint.Checks.hasCacheControlDisabled
import uk.co.baconi.oauth.api.gatling.endpoints.TokenEndpoint.Checks.hasExpiresInTwoHours
import uk.co.baconi.oauth.api.gatling.endpoints.TokenEndpoint.Checks.hasNoState
import uk.co.baconi.oauth.api.gatling.endpoints.TokenEndpoint.Checks.hasRefreshTokenAndSave
import uk.co.baconi.oauth.api.gatling.endpoints.TokenEndpoint.Checks.hasScopes
import uk.co.baconi.oauth.api.gatling.endpoints.TokenEndpoint.Checks.hasState
import uk.co.baconi.oauth.api.gatling.endpoints.TokenEndpoint.Configuration.TOKEN_ENDPOINT
import uk.co.baconi.oauth.api.gatling.feeders.Clients.Client.Type
import uk.co.baconi.oauth.api.gatling.feeders.Clients.Expressions.clientId
import uk.co.baconi.oauth.api.gatling.feeders.Clients.Expressions.clientRedirect
import uk.co.baconi.oauth.api.gatling.feeders.Clients.Expressions.clientSecret
import uk.co.baconi.oauth.api.gatling.feeders.Customers.Expressions.password
import uk.co.baconi.oauth.api.gatling.feeders.Customers.Expressions.username
import uk.co.baconi.oauth.api.gatling.feeders.ProofOfKeyCodeExchange
import uk.co.baconi.oauth.api.gatling.feeders.ProofOfKeyCodeExchange.Expressions.codeVerifier
import uk.co.baconi.oauth.api.gatling.feeders.State.Expressions.state
import uk.co.baconi.oauth.api.gatling.sessionToString

object TokenEndpoint {

    private const val ACCESS_TOKEN = "access_token"
    private const val REFRESH_TOKEN = "refresh_token"

    object Configuration {
        const val TOKEN_ENDPOINT = "/token"
    }

    object Expressions {
        val accessToken: (Session) -> String = sessionToString(ACCESS_TOKEN)
        val refreshToken: (Session) -> String = sessionToString(REFRESH_TOKEN)
    }

    object Checks {

        val hasAccessTokenAndSave: CheckBuilder.Final = jsonPath("$.access_token")
            .ofString()
            .exists()
            .saveAs(ACCESS_TOKEN)

        val hasRefreshTokenAndSave: CheckBuilder.Final = jsonPath("$.refresh_token")
            .ofString()
            .exists()
            .saveAs(REFRESH_TOKEN)

        val hasBearerTokenType: CheckBuilder.Final = jsonPath("$.token_type")
            .ofString()
            .shouldBe("bearer")

        val hasExpiresInTwoHours: CheckBuilder.Final = jsonPath("$.expires_in")
            .ofInt()
            .shouldBe(7200)

        fun hasScopes(scope: String): CheckBuilder.Final = jsonPath("$.scope")
            .ofString()
            .shouldBe(scope)

        val hasState: CheckBuilder.Final = jsonPath("$.state")
            .ofString()
            .shouldBe(state)

        val hasNoState: CheckBuilder.Final = jsonPath("$.state")
            .notExists()

        val hasCacheControlDisabled: CheckBuilder.Final = header("cache-control")
            .shouldBe("no-cache, no-store, max-age=0, must-revalidate, proxy-revalidate")
    }

    object Operations {

        private val baseAuthorisationCodeGrant = http("Authorisation Code Grant with authorisation code")
            .post(TOKEN_ENDPOINT)
            .formParam("grant_type", "authorization_code")
            .formParam("redirect_uri", clientRedirect)
            .formParam("code", authorisationCode)
            .header("content-type", "application/x-www-form-urlencoded")
            .header("accept", "application/json")
            .check(status().shouldBe(200))
            .check(hasAccessTokenAndSave)
            .check(hasRefreshTokenAndSave)
            .check(hasBearerTokenType)
            .check(hasExpiresInTwoHours)
            .check(hasScopes("basic"))
            .check(hasState)
            .check(hasCacheControlDisabled)

        /**
         * Generates an access token using a given authorisation code.
         */
        fun authorisationCodeGrant(type: Type = Type.Confidential) = when (type) {

            Type.Confidential -> baseAuthorisationCodeGrant
                .basicAuth(clientId, clientSecret)

            Type.Public -> baseAuthorisationCodeGrant
                .formParam("client_id", clientId)
                .formParam("code_verifier", codeVerifier)
        }

        /**
         * Generates an access token using a given username and password.
         */
        val passwordCredentialsGrant = http("Resource Owner Password Credentials Grant with Username and Password")
            .post(TOKEN_ENDPOINT)
            .basicAuth(clientId, clientSecret)
            .formParam("grant_type", "password")
            .formParam("username", username)
            .formParam("password", password)
            .formParam("scope", "basic")
            .header("content-type", "application/x-www-form-urlencoded")
            .header("accept", "application/json")
            .check(status().shouldBe(200))
            .check(hasAccessTokenAndSave)
            .check(hasBearerTokenType)
            .check(hasExpiresInTwoHours)
            .check(hasScopes("basic"))
            .check(hasNoState)
            .check(hasCacheControlDisabled)
    }
}