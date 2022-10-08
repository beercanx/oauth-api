package uk.co.baconi.oauth.api.endpoints

import com.fasterxml.jackson.databind.JsonNode
import TokenExchange.Checks.{hasAccessTokenAndSave, hasBearerTokenType, hasCacheControlDisabled, hasExpiresInTwoHours, hasScopes}
import TokenExchange.Configuration.endpoint
import uk.co.baconi.oauth.api.feeders.Clients.Expressions.{clientId, clientSecret}
import uk.co.baconi.oauth.api.feeders.Customers.Expressions.{password, username}
import io.gatling.core.Predef._
import io.gatling.core.check.CheckBuilder
import io.gatling.core.check.jsonpath.JsonPathCheckType
import io.gatling.core.session.Expression
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.HeaderNames.{Accept, ContentType}
import io.gatling.http.HeaderValues.{ApplicationFormUrlEncoded, ApplicationJson}
import io.gatling.http.Predef._
import io.gatling.http.check.header.HttpHeaderCheckType

object TokenExchange {

  private val ACCESS_TOKEN = "access_token"

  object Configuration {

    val endpoint = "/token"

  }

  object Expressions {

    val accessToken: Expression[String] = session => session(ACCESS_TOKEN).validate[String]

  }

  object Checks {

    val hasAccessTokenAndSave: CheckBuilder[JsonPathCheckType, JsonNode] = jsonPath("$.access_token")
      .ofType[String]
      .exists
      .saveAs(ACCESS_TOKEN)

    val hasBearerTokenType: CheckBuilder[JsonPathCheckType, JsonNode] = jsonPath("$.token_type")
      .ofType[String]
      .is("bearer")

    val hasExpiresInTwoHours: CheckBuilder[JsonPathCheckType, JsonNode] = jsonPath("$.expires_in")
      .ofType[Int]
      .is(7200)

    def hasScopes(scope: String): CheckBuilder[JsonPathCheckType, JsonNode] = jsonPath("$.scope")
      .ofType[String]
      .is(scope)

    val hasCacheControlDisabled: CheckBuilder[HttpHeaderCheckType, Response] = header("cache-control")
      .is("no-cache, no-store, max-age=0, must-revalidate, proxy-revalidate")

  }

  object Operations {

    /**
     * Generates an access token using a given username and password.
     */
    val passwordCredentialsGrant: ChainBuilder = exec(
      http("Resource Owner Password Credentials Grant Request with Username and Password")
        .post(endpoint)
        .basicAuth(clientId, clientSecret)
        .formParam("grant_type", "password")
        .formParam("username", username)
        .formParam("password", password)
        .formParam("scope", "openid")
        .header(ContentType, ApplicationFormUrlEncoded)
        .header(Accept, ApplicationJson)
        .check(status.is(200))
        .check(hasAccessTokenAndSave)
        .check(hasBearerTokenType)
        .check(hasExpiresInTwoHours)
        .check(hasScopes("openid"))
        .check(hasCacheControlDisabled)
    )

  }
}
