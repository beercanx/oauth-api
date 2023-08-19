package uk.co.baconi.oauth.api.gatling.endpoints

import io.gatling.javaapi.core.ChainBuilder
import io.gatling.javaapi.core.CoreDsl.exec
import io.gatling.javaapi.core.CoreDsl.jsonPath
import io.gatling.javaapi.core.Session
import io.gatling.javaapi.http.HttpDsl.*
import uk.co.baconi.oauth.api.gatling.endpoints.IntrospectionEndpoint.Checks.hasCacheControlDisabled
import uk.co.baconi.oauth.api.gatling.endpoints.IntrospectionEndpoint.Checks.hasClientId
import uk.co.baconi.oauth.api.gatling.endpoints.IntrospectionEndpoint.Checks.hasScopes
import uk.co.baconi.oauth.api.gatling.endpoints.IntrospectionEndpoint.Checks.hasSubject
import uk.co.baconi.oauth.api.gatling.endpoints.IntrospectionEndpoint.Checks.hasUsername
import uk.co.baconi.oauth.api.gatling.endpoints.IntrospectionEndpoint.Checks.isActive
import uk.co.baconi.oauth.api.gatling.endpoints.IntrospectionEndpoint.Configuration.INTROSPECTION_ENDPOINT
import uk.co.baconi.oauth.api.gatling.endpoints.TokenEndpoint.Expressions.accessToken
import uk.co.baconi.oauth.api.gatling.feeders.Clients.Expressions.clientId
import uk.co.baconi.oauth.api.gatling.feeders.Clients.Expressions.clientSecret
import uk.co.baconi.oauth.api.gatling.feeders.Customers.Expressions.username

object IntrospectionEndpoint {

    object Configuration {
        const val INTROSPECTION_ENDPOINT = "/introspect"
    }

    object Checks {

        val isActive = jsonPath("$.active")
            .ofBoolean()
            .shouldBe(true)

        fun hasClientId(clientId: (Session) -> String) = jsonPath("$.client_id")
            .ofString()
            .shouldBe(clientId)

        fun hasUsername(username: (Session) -> String) = jsonPath("$.username")
            .ofString()
            .shouldBe(username)

        fun hasSubject(subject: (Session) -> String) = jsonPath("$.sub")
            .ofString()
            .shouldBe(subject)

        fun hasScopes(scope: String) = jsonPath("$.scope")
            .ofString()
            .shouldBe(scope)

        val hasCacheControlDisabled = header("cache-control")
            .shouldBe("no-cache, no-store, max-age=0, must-revalidate, proxy-revalidate")
    }

    object Operations {

        /**
         * Introspects the access token in the session.
         */
        val introspectAccessToken: ChainBuilder = exec(
            http("Introspection Request with Access Token")
                .post(INTROSPECTION_ENDPOINT)
                .basicAuth(clientId, clientSecret)
                .formParam("token", accessToken)
                .asFormUrlEncoded()
                .header("accept", "application/json")
                .check(status().shouldBe(200))
                .check(isActive)
                .check(hasClientId(clientId))
                .check(hasUsername(username))
                .check(hasSubject(username))
                .check(hasScopes("basic"))
                .check(hasCacheControlDisabled)
        )
    }
}