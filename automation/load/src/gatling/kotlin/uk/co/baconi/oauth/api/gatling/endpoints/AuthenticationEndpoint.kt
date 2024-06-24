package uk.co.baconi.oauth.api.gatling.endpoints

import io.gatling.http.client.uri.Uri
import io.gatling.javaapi.core.ChainBuilder
import io.gatling.javaapi.core.CheckBuilder
import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.Session
import io.gatling.javaapi.http.HttpDsl.*
import io.gatling.javaapi.http.HttpRequestActionBuilder
import uk.co.baconi.oauth.api.gatling.endpoints.AuthenticationEndpoint.Checks.hasCacheControlDisabled
import uk.co.baconi.oauth.api.gatling.endpoints.AuthenticationEndpoint.Checks.hasCsrfTokenAndSave
import uk.co.baconi.oauth.api.gatling.endpoints.AuthenticationEndpoint.Checks.hasExpectedUsername
import uk.co.baconi.oauth.api.gatling.endpoints.AuthenticationEndpoint.Checks.hasSuccessType
import uk.co.baconi.oauth.api.gatling.endpoints.AuthenticationEndpoint.Configuration.AUTHENTICATION_ENDPOINT
import uk.co.baconi.oauth.api.gatling.endpoints.AuthenticationEndpoint.Configuration.AUTHENTICATION_SESSION_ENDPOINT
import uk.co.baconi.oauth.api.gatling.endpoints.AuthenticationEndpoint.Expressions.csrfToken
import uk.co.baconi.oauth.api.gatling.feeders.Clients.Expressions.clientId
import uk.co.baconi.oauth.api.gatling.feeders.Clients.Expressions.clientRedirect
import uk.co.baconi.oauth.api.gatling.feeders.Customers.Expressions.password
import uk.co.baconi.oauth.api.gatling.feeders.Customers.Expressions.username
import uk.co.baconi.oauth.api.gatling.sessionToString

object AuthenticationEndpoint {

    private const val CSRF_TOKEN = "csrfToken"

    object Configuration {
        const val AUTHENTICATION_ENDPOINT = "/authentication"
        const val AUTHENTICATION_SESSION_ENDPOINT = "/authentication/session"
    }

    object Expressions {
        val csrfToken: (Session) -> String = sessionToString(CSRF_TOKEN)
    }

    object Checks {

        val hasCsrfTokenAndSave: CheckBuilder.Final = jsonPath("$.csrfToken")
            .ofString()
            .exists()
            .saveAs(CSRF_TOKEN)

        val hasSuccessType: CheckBuilder.Final = jsonPath("$.type")
            .ofString()
            .shouldBe("success")

        val hasExpectedUsername: CheckBuilder.Final = jsonPath("$.username")
            .ofString()
            .shouldBe(username)

        val hasCacheControlDisabled: CheckBuilder.Final = header("cache-control")
            .shouldBe("no-cache, no-store, max-age=0, must-revalidate, proxy-revalidate")
    }

    object Operations {

        val authenticate: ChainBuilder = exec(

            http("CSRF token request for use with an authentication request.")
                .get(AUTHENTICATION_SESSION_ENDPOINT)
                .check(status().shouldBe(200))
                .check(hasCsrfTokenAndSave)
                .check(hasCacheControlDisabled),

            http("Authentication request with username and password.")
                .post(AUTHENTICATION_ENDPOINT)
                .disableFollowRedirect()
                .asJson()
                .body(StringBody { session ->
                    """|{
                       |"username": "${username(session)}",
                       |"password": ${password(session).toCharArray().contentToString()},
                       |"csrfToken": "${csrfToken(session)}"
                       |}""".trimMargin()
                })
                .header("content-type", "application/json")
                .header("accept", "application/json")
                .check(status().shouldBe(200))
                .check(hasSuccessType)
                .check(hasExpectedUsername)
                .check(hasCacheControlDisabled)
        )
    }
}