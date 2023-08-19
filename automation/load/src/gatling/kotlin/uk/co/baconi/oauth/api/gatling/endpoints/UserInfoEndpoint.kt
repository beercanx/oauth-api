package uk.co.baconi.oauth.api.gatling.endpoints

import io.gatling.javaapi.core.ChainBuilder
import io.gatling.javaapi.core.CheckBuilder
import io.gatling.javaapi.core.CoreDsl.exec
import io.gatling.javaapi.core.CoreDsl.jsonPath
import io.gatling.javaapi.core.Session
import io.gatling.javaapi.http.HttpDsl.*
import uk.co.baconi.oauth.api.gatling.endpoints.TokenEndpoint.Expressions.accessToken
import uk.co.baconi.oauth.api.gatling.endpoints.UserInfoEndpoint.Checks.hasCacheControlDisabled
import uk.co.baconi.oauth.api.gatling.endpoints.UserInfoEndpoint.Checks.hasSubject
import uk.co.baconi.oauth.api.gatling.endpoints.UserInfoEndpoint.Configuration.USER_INFO_ENDPOINT
import uk.co.baconi.oauth.api.gatling.feeders.Customers.Expressions.username

class UserInfoEndpoint {

    object Configuration {
        const val USER_INFO_ENDPOINT = "/userinfo"
    }

    object Checks {

        fun hasSubject(subject: (Session) -> String): CheckBuilder.Final = jsonPath("$.sub")
            .ofString()
            .shouldBe(subject)

        val hasCacheControlDisabled: CheckBuilder.Final = header("cache-control")
            .shouldBe("no-cache, no-store, max-age=0, must-revalidate, proxy-revalidate")
    }

    object Operations {

        /**
         * Checks the Claim data in the User Info endpoint using the access token in the session.
         */
        val userInfoWithAccessToken: ChainBuilder = exec(
            http("User Info Request with Access Token")
                .get(USER_INFO_ENDPOINT)
                .header("authorization", "Bearer #{access_token}")
                .header("accept", "application/json")
                .check(status().shouldBe(200))
                .check(hasSubject(username))
                .check(hasCacheControlDisabled)
        )
    }
}