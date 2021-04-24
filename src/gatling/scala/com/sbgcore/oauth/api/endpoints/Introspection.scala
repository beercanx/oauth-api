package com.sbgcore.oauth.api.endpoints

import com.fasterxml.jackson.databind.JsonNode
import com.sbgcore.oauth.api.endpoints.Introspection.Checks._
import com.sbgcore.oauth.api.endpoints.Introspection.Configuration.endpoint
import com.sbgcore.oauth.api.endpoints.Introspection.Expressions.introspectionBody
import com.sbgcore.oauth.api.endpoints.TokenExchange.Expressions.accessToken
import com.sbgcore.oauth.api.feeders.Clients.Expressions.{clientId, clientSecret}
import com.sbgcore.oauth.api.feeders.Customers.Expressions.username
import io.gatling.core.Predef._
import io.gatling.core.check.CheckBuilder
import io.gatling.core.check.jsonpath.JsonPathCheckType
import io.gatling.core.session.Expression
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.HeaderNames.{Accept, ContentType}
import io.gatling.http.HeaderValues.ApplicationJson
import io.gatling.http.Predef._

object Introspection {

  object Configuration {

    val endpoint = "/openid/v1/introspect"

  }

  object Expressions {

    val introspectionBody: Expression[String] = session => for {
      accessToken <- accessToken(session)
    } yield s"""{ "token": "$accessToken" }"""

  }

  object Checks {

    val isActive: CheckBuilder[JsonPathCheckType, JsonNode, Boolean] = jsonPath("active")
      .ofType[Boolean]
      .is(true)

    def hasClientId(clientId: Expression[String]): CheckBuilder[JsonPathCheckType, JsonNode, String] = jsonPath("client_id")
      .ofType[String]
      .is(clientId)

    def hasUsername(username: Expression[String]): CheckBuilder[JsonPathCheckType, JsonNode, String] = jsonPath("username")
      .ofType[String]
      .is(username)

    def hasSubject(subject: Expression[String]): CheckBuilder[JsonPathCheckType, JsonNode, String] = jsonPath("sub")
      .ofType[String]
      .is(subject)

    def hasScope(scope: Expression[String]): CheckBuilder[JsonPathCheckType, JsonNode, String] = jsonPath("scope")
      .ofType[String]
      .is(scope)
  }

  object Operations {

    /**
     * Introspects the access token in the session.
     *
     * TODO - hasCacheControlDisabled?
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
    )

  }
}
