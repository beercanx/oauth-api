package com.sbgcore.oauth.api.endpoints

import com.fasterxml.jackson.databind.JsonNode
import com.sbgcore.oauth.api.endpoints.TokenExchange.Checks.{hasAccessToken, hasBearerTokenType, hasExpiresInTwoHours, hasScopes}
import com.sbgcore.oauth.api.endpoints.TokenExchange.Configuration.endpoint
import com.sbgcore.oauth.api.feeders.Clients.Expressions.{clientId, clientSecret}
import com.sbgcore.oauth.api.feeders.Customers.Expressions.{password, username}
import io.gatling.core.Predef._
import io.gatling.core.check.jsonpath.JsonPathCheckType
import io.gatling.core.check.{Check, CheckBuilder}
import io.gatling.core.session.Expression
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.HeaderNames.{Accept, ContentType}
import io.gatling.http.HeaderValues.{ApplicationFormUrlEncoded, ApplicationJson}
import io.gatling.http.Predef._

object TokenExchange {

  private val ACCESS_TOKEN = "access_token"
  private val SCOPES = "scopes"

  object Configuration {

    val endpoint = "/openid/v1/token"

  }

  object Expressions {

    val accessToken: Expression[String] = session => session(ACCESS_TOKEN).validate[String]

  }

  object Checks {

    val hasAccessToken: CheckBuilder[JsonPathCheckType, JsonNode, String] = jsonPath("access_token")
      .ofType[String]
      .exists
      .saveAs(ACCESS_TOKEN)

    val hasBearerTokenType: CheckBuilder[JsonPathCheckType, JsonNode, String] = jsonPath("token_type")
      .ofType[String]
      .is("bearer")

    val hasExpiresInTwoHours: CheckBuilder[JsonPathCheckType, JsonNode, Int] = jsonPath("expires_in")
      .ofType[Int]
      .is(7200)

    val hasScopes: CheckBuilder[JsonPathCheckType, JsonNode, String] = jsonPath("scope")
      .ofType[String]
      .exists

  }

  object Operations {

    /**
     * Generates an access token using a given username and password.
     *
     * TODO - hasCacheControlDisabled?
     */
    val passwordFlow: ChainBuilder = exec(
      http("Password Flow Request with Username and Password")
        .post(endpoint)
        .basicAuth(clientId, clientSecret)
        .formParam("username", username)
        .formParam("password", password)
        .header(ContentType, ApplicationFormUrlEncoded)
        .header(Accept, ApplicationJson)
        .check(status.is(200))
        .check(hasAccessToken)
        .check(hasBearerTokenType)
        .check(hasExpiresInTwoHours)
        .check(hasScopes)
    )

  }
}
