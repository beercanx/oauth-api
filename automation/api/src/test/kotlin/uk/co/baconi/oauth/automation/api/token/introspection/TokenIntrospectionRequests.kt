package uk.co.baconi.oauth.automation.api.token.introspection

import com.typesafe.config.ConfigFactory
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.http.Method
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE
import org.junit.jupiter.params.provider.EnumSource.Mode.INCLUDE
import uk.co.baconi.oauth.automation.api.RFC6749
import uk.co.baconi.oauth.automation.api.RFC6750
import uk.co.baconi.oauth.automation.api.RFC7662
import uk.co.baconi.oauth.automation.api.TOKEN_INTROSPECTION
import uk.co.baconi.oauth.automation.api.driver.WithRestAssuredDriver
import java.util.*

@Tag(RFC7662)
@Tag(TOKEN_INTROSPECTION)
class TokenIntrospectionRequests : WithRestAssuredDriver {

    private val config = ConfigFactory.load().getConfig("uk.co.baconi.oauth.automation.api.token.introspection")
    private val location = config.getString("location")

    @ParameterizedTest
    @EnumSource(Method::class, mode = EXCLUDE, names = ["POST"])
    fun `should allow only post requests`(method: Method) {
        given(driver.rest)
            .request(method, location)
            .then()
            .statusCode(405)
            .expectNoWwwAuthenticateHeader()
            .body(emptyString())
    }

    /**
     * To prevent token scanning attacks, the endpoint MUST also require some form of authorization to access this
     * endpoint, such as client authentication as described in OAuth 2.0 [RFC6749] or a separate OAuth 2.0 access token
     * such as the bearer token described in OAuth 2.0 Bearer Token Usage [RFC6750].
     */
    @Nested
    @DisplayName("must allow only authorised requests")
    inner class MustAllowOnlyAuthorisedRequests {

        @Test
        fun `reject missing authentication`() {

            given(driver.rest)
                .post(location)
                .then()
                .statusCode(401)
                .header("WWW-Authenticate", "Basic realm=oauth-api, charset=UTF-8")
                .body(emptyString())
        }

        @Test
        fun `reject invalid basic authentication`() {

            given(driver.rest)
                .auth().basic("aardvark", "badger")
                .post(location)
                .then()
                .statusCode(401)
                .header("WWW-Authenticate", "Basic realm=oauth-api, charset=UTF-8")
                .body(emptyString())
        }

        @Test
        fun `reject public client authentication`() {

            given(driver.rest)
                .urlEncodingEnabled(true)
                .params(
                    mapOf(
                        "client_id" to "consumer-y"
                    )
                )
                .post(location)
                .then()
                .statusCode(401)
                .header("WWW-Authenticate", "Basic realm=oauth-api, charset=UTF-8")
                .body(emptyString())
        }

        @Test
        fun `reject a valid client that is missing the introspection allowed action`() {

            given(driver.rest)
                .auth().basic("no-op", "MQgQKBW3*j1m4QyHWnMsp52sqADHq7j3")
                .urlEncodingEnabled(true)
                .params(
                    mapOf(
                        "token" to UUID.randomUUID().toString()
                    )
                )
                .post(location)
                .then()
                .statusCode(403)
                .expectNoWwwAuthenticateHeader()
                .contentType(ContentType.JSON)
                .body(
                    "error", equalTo("unauthorized_client"),
                    "description", equalTo("client is not allowed to introspect")
                )
        }

        @Test
        fun `accept a valid client using basic authentication`() {

            given(driver.rest)
                .withValidClient()
                .post(location)
                .then()
                .statusCode(400)
                .expectNoWwwAuthenticateHeader()
                .contentType(ContentType.JSON)
                .body(
                    "error", equalTo("invalid_request"),
                    "description", equalTo("missing parameter: token")
                )
        }
    }

    @Nested
    @DisplayName("should allow only url encoded form requests")
    inner class ShouldAllowOnlyUrlEncodedFormRequests {

        @Test
        fun `reject JSON body requests`() {

            given(driver.rest)
                .withValidClient()
                .contentType(ContentType.JSON)
                .body("""{ "token": "${UUID.randomUUID()}" }""")
                .post(location)
                .then()
                .statusCode(415)
                .expectNoWwwAuthenticateHeader()
                .body(emptyString())
        }

        @Test
        fun `reject XML body requests`() {

            given(driver.rest)
                .withValidClient()
                .contentType(ContentType.XML)
                .body("<token>${UUID.randomUUID()}</token>")
                .post(location)
                .then()
                .statusCode(415)
                .expectNoWwwAuthenticateHeader()
                .body(emptyString())
        }

        @ParameterizedTest
        @EnumSource(ContentType::class, mode = INCLUDE, names = ["TEXT", "JSON", "XML", "HTML"])
        fun `reject non url encoded form posts`(contentType: ContentType) {

            given(driver.rest)
                .withValidClient()
                .contentType(contentType)
                .post(location)
                .then()
                .statusCode(415)
                .expectNoWwwAuthenticateHeader()
                .body(emptyString())
        }

        @Test
        fun `accept URL encoded form body requests`() {

            given(driver.rest)
                .withValidClient()
                .contentType(ContentType.URLENC)
                .formParams(
                    mapOf(
                        "token" to UUID.randomUUID().toString()
                    )
                )
                .post(location)
                .then()
                .statusCode(200)
                .expectNoWwwAuthenticateHeader()
                .contentType(ContentType.JSON)
                .body("active", equalTo(false))
        }
    }

    private fun RequestSpecification.withValidClient() = auth().basic("consumer-x", "9VylF3DbEeJbtdbih3lqpNXBw@Non#bi")

    private fun ValidatableResponse.expectNoWwwAuthenticateHeader() = header("WWW-Authenticate", nullValue())

}