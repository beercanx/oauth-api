package uk.co.baconi.oauth.automation.api.driver

import com.typesafe.config.Config
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import org.hamcrest.Matchers.equalTo
import uk.co.baconi.oauth.automation.api.config.Client
import uk.co.baconi.oauth.automation.api.config.ConfidentialClient
import uk.co.baconi.oauth.automation.api.getUri
import uk.co.baconi.oauth.automation.api.isUUID
import java.net.URI

interface TokenEndpoint {

    val config: Config
    val browserSpecification: RequestSpecification

    val tokenLocation: URI
        get() = config.getUri("token.location")

    fun authorisationCodeGrant(client: ConfidentialClient, code: String): ValidatableResponse {

        return given(browserSpecification)
            .auth().preemptive().basic(client)
            .contentType(ContentType.URLENC)
            .formParams(
                mapOf(
                    "grant_type" to "authorization_code",
                    "redirect_uri" to client.redirectUri.toASCIIString(),
                    "code" to code,
                )
            )
            .post(tokenLocation)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("access_token", isUUID())
            .body("refresh_token", isUUID())
            .body("token_type", equalTo("bearer"))
            .body("expires_in", equalTo(7200))
    }
}