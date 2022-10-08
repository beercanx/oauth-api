package uk.co.baconi.oauth.api.endpoints

import com.fasterxml.jackson.databind.JsonNode
import uk.co.baconi.oauth.api.OAuth2.Expressions.bearerAuth
import TokenExchange.Expressions.accessToken
import UserInfo.Checks.hasSubject
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

object UserInfo {

  object Configuration {

    val endpoint = "/openid/v1/userinfo"

  }

  object Checks {

    def hasSubject(subject: Expression[String]): CheckBuilder[JsonPathCheckType, JsonNode, String] = jsonPath("$.sub")
      .ofType[String]
      .is(subject)
  }

  object Operations {

    /**
     * Checks the Claim data in the User Info endpoint using the access token in the session.
     *
     * TODO - hasCacheControlDisabled?
     */
    val userInfoWithAccessToken: ChainBuilder = exec(
      http("User Info Request with Access Token")
        .get(endpoint)
        .header(Authorization, bearerAuth(accessToken))
        .header(Accept, ApplicationJson)
        .check(status.is(200))
        .check(hasSubject(username))
    )
  }
}
