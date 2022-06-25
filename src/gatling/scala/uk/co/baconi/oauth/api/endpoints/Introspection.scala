package uk.co.baconi.oauth.api.endpoints

import com.fasterxml.jackson.databind.JsonNode
import Introspection.Checks._
import Introspection.Configuration.endpoint
import Introspection.Expressions.introspectionBody
import TokenExchange.Expressions.accessToken
import uk.co.baconi.oauth.api.feeders.Clients.Expressions.{clientId, clientSecret}
import uk.co.baconi.oauth.api.feeders.Customers.Expressions.username
import io.gatling.core.Predef._
import io.gatling.core.check.CheckBuilder
import io.gatling.core.check.jsonpath.JsonPathCheckType
import io.gatling.core.session.Expression
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.HeaderNames.{Accept, ContentType}
import io.gatling.http.HeaderValues.ApplicationJson
import io.gatling.http.Predef._
import io.gatling.http.check.header.HttpHeaderCheckType

object Introspection {

  object Configuration {

    val endpoint = "/oauth/v1/introspect"

  }

  object Expressions {

    val introspectionBody: Expression[String] = session => for {
      accessToken <- accessToken(session)
    } yield s"""{ "token": "$accessToken" }"""

  }

  object Checks {

    val isActive: CheckBuilder[JsonPathCheckType, JsonNode] = jsonPath("$.active")
      .ofType[Boolean]
      .is(true)

    def hasClientId(clientId: Expression[String]): CheckBuilder[JsonPathCheckType, JsonNode] = jsonPath("$.client_id")
      .ofType[String]
      .is(clientId)

    def hasUsername(username: Expression[String]): CheckBuilder[JsonPathCheckType, JsonNode] = jsonPath("$.username")
      .ofType[String]
      .is(username)

    def hasSubject(subject: Expression[String]): CheckBuilder[JsonPathCheckType, JsonNode] = jsonPath("$.sub")
      .ofType[String]
      .is(subject)

    def hasScope(scope: Expression[String]): CheckBuilder[JsonPathCheckType, JsonNode] = jsonPath("$.scope")
      .ofType[String]
      .is(scope)

    val hasCacheControlDisabled: CheckBuilder[HttpHeaderCheckType, Response] = header("cache-control")
      .is("no-cache, no-store, max-age=0, must-revalidate, proxy-revalidate")
  }

  object Operations {

    /**
     * Introspects the access token in the session.
     */
    val introspectAccessToken: ChainBuilder = exec(
      http("Introspection Request with Access Token")
        .post(endpoint)
        .basicAuth(clientId, clientSecret)
        .body(StringBody(introspectionBody))
        .header(ContentType, ApplicationJson)
        .header(Accept, ApplicationJson)
        .check(status.is(200))
        .check(isActive)
        .check(hasClientId(clientId))
        .check(hasUsername(username))
        .check(hasSubject(username))
        .check(hasScope("openid"))
        .check(hasCacheControlDisabled)
    )

  }
}
