package uk.co.baconi.oauth.automation.api.token.introspection

import com.typesafe.config.ConfigFactory
import io.kotest.matchers.string.beEmpty
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.http.Method
import io.restassured.specification.RequestSpecification
import org.hamcrest.Matchers.emptyString
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE
import uk.co.baconi.oauth.automation.api.*
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
                .body(emptyString())
        }

        @Test
        fun `reject invalid basic authentication`() {

            given(driver.rest)
                .auth().basic("aardvark", "badger")
                .post(location)
                .then()
                .statusCode(401)
                .body(emptyString())
        }

        @Test
        fun `accept valid basic authentication`() {

            given(driver.rest)
                .withValidClient()
                .post(location)
                .then()
                .statusCode(400)
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
                .body(emptyString())
        }

        @Test
        fun `accept URL encoded form body requests`() {

            given(driver.rest)
                .withValidClient()
                .urlEncodingEnabled(true)
                .params(mapOf(
                    "token" to UUID.randomUUID().toString()
                ))
                .post(location)
                .then()
                .statusCode(200)
                .body("active", equalTo(false))
        }
    }

    private fun RequestSpecification.withValidClient() = auth().basic("consumer-x", "9VylF3DbEeJbtdbih3lqpNXBw@Non#bi")

}