package uk.co.baconi.oauth.api.gatling.endpoints

import io.gatling.http.client.uri.Uri
import io.gatling.javaapi.core.CheckBuilder
import io.gatling.javaapi.core.CoreDsl.css
import io.gatling.javaapi.core.Session
import io.gatling.javaapi.http.HttpDsl.*
import uk.co.baconi.oauth.api.gatling.endpoints.AuthorisationEndpoint.Checks.hasAuthenticationPageTitle
import uk.co.baconi.oauth.api.gatling.endpoints.AuthorisationEndpoint.Checks.hasAuthorisationCodeAndSave
import uk.co.baconi.oauth.api.gatling.endpoints.AuthorisationEndpoint.Checks.hasCacheControlDisabled
import uk.co.baconi.oauth.api.gatling.endpoints.AuthorisationEndpoint.Configuration.AUTHORISATION_ENDPOINT
import uk.co.baconi.oauth.api.gatling.endpoints.AuthorisationEndpoint.Transforms.headerToAuthorisationCode
import uk.co.baconi.oauth.api.gatling.feeders.Clients.Client.Type
import uk.co.baconi.oauth.api.gatling.feeders.Clients.Expressions.clientId
import uk.co.baconi.oauth.api.gatling.feeders.Clients.Expressions.clientRedirect
import uk.co.baconi.oauth.api.gatling.feeders.ProofOfKeyCodeExchange.Expressions.codeChallenge
import uk.co.baconi.oauth.api.gatling.feeders.ProofOfKeyCodeExchange.Expressions.codeChallengeMethod
import uk.co.baconi.oauth.api.gatling.feeders.State.Expressions.state
import uk.co.baconi.oauth.api.gatling.sessionToString
import java.net.URI

object AuthorisationEndpoint {

    private const val AUTHORISATION_CODE = "authorisation_code"

    object Configuration {
        const val AUTHORISATION_ENDPOINT = "/authorise"
    }

    object Expressions {
        val authorisationCode: (Session) -> String = sessionToString(AUTHORISATION_CODE)
    }

    object Transforms {

        private val URI.queryParams: Map<String, String>
            get() = query.split("&").map { it.split("=", limit = 2) }.associate { it[0] to it[1] }

        val headerToAuthorisationCode: (String?) -> String? = { header ->
            when (header) {
                null -> null
                else -> URI.create(header).queryParams["code"]
            }
        }
    }

    object Checks {

        val hasAuthenticationPageTitle = css("title")
            .shouldBe("Login Page") // TODO - Correct in application

        val hasAuthorisationCodeAndSave: CheckBuilder.Final = header("location")
            .transform(headerToAuthorisationCode)
            .exists()
            .saveAs(AUTHORISATION_CODE)

        val hasCacheControlDisabled: CheckBuilder.Final = header("cache-control")
            .shouldBe("no-cache, no-store, max-age=0, must-revalidate, proxy-revalidate")
    }

    object Operations {

        private fun baseConfidentialAuthorise(type: Type) = http("Authorisation request with confidential client")
            .get(AUTHORISATION_ENDPOINT)
            .disableFollowRedirect()
            .queryParam("response_type", "code")
            .queryParam("client_id", clientId)
            .queryParam("redirect_uri", clientRedirect)
            .queryParam("state", state)
            .queryParam("scope", "basic")
            .let { request ->
                when(type) {
                    Type.Confidential -> request
                    Type.Public -> request
                        .queryParam("code_challenge", codeChallenge)
                        .queryParam("code_challenge_method", codeChallengeMethod)
                }
            }

        /**
         * Starts an authorisation request and expects the authentication page.
         */
        fun confidentialAuthorisationWithPage(type: Type) = baseConfidentialAuthorise(type)
            .check(status().shouldBe(200))
            .check(hasAuthenticationPageTitle)
            .check(hasCacheControlDisabled)

        /**
         * Starts an authorisation request and expects callback with an authorisation code.
         */
        fun confidentialAuthorisationWithCode(type: Type) = baseConfidentialAuthorise(type)
            .check(status().shouldBe(302))
            .check(hasAuthorisationCodeAndSave)
            .check(hasCacheControlDisabled)
    }
}