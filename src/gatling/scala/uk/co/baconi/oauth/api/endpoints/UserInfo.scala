package uk.co.baconi.oauth.api.endpoints

import com.fasterxml.jackson.databind.JsonNode
import uk.co.baconi.oauth.api.OAuth2.Expressions.bearerAuth
import TokenExchange.Expressions.accessToken
import UserInfo.Checks.{hasCacheControlDisabled, hasSubject}
import UserInfo.Configuration.endpoint
import uk.co.baconi.oauth.api.feeders.Customers.Expressions.username
import io.gatling.core.Predef._
import io.gatling.core.check.CheckBuilder
import io.gatling.core.check.jsonpath.JsonPathCheckType
import io.gatling.core.session.Expression
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.HeaderNames.{Accept, Authorization}
import io.gatling.http.HeaderValues.ApplicationJson
import io.gatling.http.Predef._
import io.gatling.http.check.header.HttpHeaderCheckType

object UserInfo {

  object Configuration {

    val endpoint = "/oauth/v1/userinfo"

  }

  object Checks {

    def hasSubject(subject: Expression[String]): CheckBuilder[JsonPathCheckType, JsonNode] = jsonPath("$.sub")
      .ofType[String]
      .is(subject)

    val hasCacheControlDisabled: CheckBuilder[HttpHeaderCheckType, Response] = header("cache-control")
      .is("no-cache, no-store, max-age=0, must-revalidate, proxy-revalidate")

  }

  object Operations {

    /**
     * Checks the Claim data in the User Info endpoint using the access token in the session.
     */
    val userInfoWithAccessToken: ChainBuilder = exec(
      http("User Info Request with Access Token")
        .get(endpoint)
        .header(Authorization, bearerAuth(accessToken))
        .header(Accept, ApplicationJson)
        .check(status.is(200))
        .check(hasSubject(username))
        .check(hasCacheControlDisabled)
    )
  }
}
