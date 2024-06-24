package uk.co.baconi.oauth.api.gatling.endpoints

import io.gatling.http.client.uri.Uri
import io.gatling.javaapi.core.ChainBuilder
import io.gatling.javaapi.core.CheckBuilder
import io.gatling.javaapi.core.CoreDsl.css
import io.gatling.javaapi.core.CoreDsl.exec
import io.gatling.javaapi.core.Session
import io.gatling.javaapi.http.HttpDsl.*
import uk.co.baconi.oauth.api.gatling.endpoints.AuthorisationEndpoint.Checks.hasAuthenticationPageTitle
import uk.co.baconi.oauth.api.gatling.endpoints.AuthorisationEndpoint.Checks.hasAuthorisationCodeAndSave
import uk.co.baconi.oauth.api.gatling.endpoints.AuthorisationEndpoint.Checks.hasCacheControlDisabled
import uk.co.baconi.oauth.api.gatling.endpoints.AuthorisationEndpoint.Configuration.AUTHORISATION_ENDPOINT
import uk.co.baconi.oauth.api.gatling.endpoints.AuthorisationEndpoint.Transforms.headerToAuthorisationCode
import uk.co.baconi.oauth.api.gatling.feeders.Clients.Expressions.clientId
import uk.co.baconi.oauth.api.gatling.feeders.Clients.Expressions.clientRedirect
import uk.co.baconi.oauth.api.gatling.feeders.State.Expressions.state
import uk.co.baconi.oauth.api.gatling.sessionToString

object AuthorisationEndpoint {

    private const val AUTHORISATION_CODE = "authorisation_code"

    object Configuration {
        const val AUTHORISATION_ENDPOINT = "/authorise"
    }

    object Expressions {
        val authorisationCode: (Session) -> String = sessionToString(AUTHORISATION_CODE)
    }

    object Transforms {
        val headerToAuthorisationCode: (String?) -> String? = { header ->
            when (header) {
                null -> null
                else -> Uri.create(header).encodedQueryParams.find { param -> param.name == "code" }?.value
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

        private val baseAuthorise = http("Authorisation request with client id, redirect and state")
            .get(AUTHORISATION_ENDPOINT)
            .disableFollowRedirect()
            .queryParam("response_type", "code")
            .queryParam("client_id", clientId)
            .queryParam("redirect_uri", clientRedirect)
            .queryParam("state", state)
            .queryParam("scope", "basic")

        /**
         * Starts an authorisation request and expects the authentication page.
         */
        val authoriseWithAuthenticationPage: ChainBuilder = exec(
            baseAuthorise
                .check(status().shouldBe(200))
                .check(hasAuthenticationPageTitle)
                .check(hasCacheControlDisabled)
        )

        /**
         * Starts an authorisation request and expects callback with an authorisation code.
         */
        val authoriseWithAuthorisationCode: ChainBuilder = exec(
            baseAuthorise
                .check(status().shouldBe(302))
                .check(hasAuthorisationCodeAndSave)
                .check(hasCacheControlDisabled)
        )
    }
}