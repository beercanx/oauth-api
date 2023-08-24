package uk.co.baconi.oauth.automation.api.token.introspection

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.http.Method
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE
import org.junit.jupiter.params.provider.EnumSource.Mode.INCLUDE
import uk.co.baconi.oauth.automation.api.*
import uk.co.baconi.oauth.automation.api.config.AccessToken
import uk.co.baconi.oauth.automation.api.config.Client
import uk.co.baconi.oauth.automation.api.driver.RestAssuredDriverTest
import uk.co.baconi.oauth.automation.api.driver.basic
import java.util.*

@Tag(RFC7662)
@Tag(AUTOMATION)
@Tag(TOKEN_INTROSPECTION)
class TokenIntrospectionRequests : RestAssuredDriverTest() {

    @ParameterizedTest
    @EnumSource(Method::class, mode = EXCLUDE, names = ["POST"])
    fun `should allow only post requests`(method: Method) {
        given(driver.serverSpecification)
            .request(method, driver.introspectionLocation)
            .then()
            .statusCode(405)
            .header("WWW-Authenticate", nullValue())
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

            given(driver.serverSpecification)
                .post(driver.introspectionLocation)
                .then()
                .statusCode(401)
                .header("WWW-Authenticate", "Basic realm=oauth-api, charset=UTF-8")
                .body(emptyString())
        }

        @Test
        fun `reject invalid basic authentication`() {

            given(driver.serverSpecification)
                .auth().basic("aardvark", "badger")
                .post(driver.introspectionLocation)
                .then()
                .statusCode(401)
                .header("WWW-Authenticate", "Basic realm=oauth-api, charset=UTF-8")
                .body(emptyString())
        }

        @Test
        fun `reject public client authentication`() {

            given(driver.serverSpecification)
                .urlEncodingEnabled(true)
                .params(
                    mapOf(
                        "client_id" to "consumer-y"
                    )
                )
                .post(driver.introspectionLocation)
                .then()
                .statusCode(401)
                .header("WWW-Authenticate", "Basic realm=oauth-api, charset=UTF-8")
                .body(emptyString())
        }

        @Test
        fun `reject a valid client that is missing the introspection allowed action`() {

            given(driver.serverSpecification)
                .auth().basic("no-op", "MQgQKBW3*j1m4QyHWnMsp52sqADHq7j3")
                .urlEncodingEnabled(true)
                .params(
                    mapOf(
                        "token" to UUID.randomUUID().toString()
                    )
                )
                .post(driver.introspectionLocation)
                .then()
                .statusCode(403)
                .header("WWW-Authenticate", nullValue())
                .contentType(ContentType.JSON)
                .body(
                    "error", equalTo("unauthorized_client"),
                    "error_description", equalTo("client is not allowed to introspect")
                )
        }

        @Test
        fun `accept a valid client using basic authentication`(client: Client) {

            given(driver.serverSpecification)
                .auth().basic(client)
                .post(driver.introspectionLocation)
                .then()
                .statusCode(400)
                .header("WWW-Authenticate", nullValue())
                .contentType(ContentType.JSON)
                .body(
                    "error", equalTo("invalid_request"),
                    "error_description", equalTo("missing parameter: token")
                )
        }
    }

    @Nested
    @DisplayName("should allow only url encoded form requests")
    inner class ShouldAllowOnlyUrlEncodedFormRequests {

        @Test
        fun `reject JSON body requests`(client: Client) {

            given(driver.serverSpecification)
                .auth().basic(client)
                .contentType(ContentType.JSON)
                .body("""{ "token": "${UUID.randomUUID()}" }""")
                .post(driver.introspectionLocation)
                .then()
                .statusCode(415)
                .header("WWW-Authenticate", nullValue())
                .body(emptyString())
        }

        @Test
        fun `reject XML body requests`(client: Client) {

            given(driver.serverSpecification)
                .auth().basic(client)
                .contentType(ContentType.XML)
                .body("<token>${UUID.randomUUID()}</token>")
                .post(driver.introspectionLocation)
                .then()
                .statusCode(415)
                .header("WWW-Authenticate", nullValue())
                .body(emptyString())
        }

        @ParameterizedTest
        @EnumSource(ContentType::class, mode = INCLUDE, names = ["TEXT", "JSON", "XML", "HTML"])
        fun `reject non url encoded form posts`(contentType: ContentType, client: Client) {

            given(driver.serverSpecification)
                .auth().basic(client)
                .contentType(contentType)
                .post(driver.introspectionLocation)
                .then()
                .statusCode(415)
                .header("WWW-Authenticate", nullValue())
                .body(emptyString())
        }

        @Test
        fun `accept URL encoded form body requests`(client: Client) {

            driver.introspect(client, AccessToken(UUID.randomUUID()))
                .body("active", equalTo(false))
        }
    }
}