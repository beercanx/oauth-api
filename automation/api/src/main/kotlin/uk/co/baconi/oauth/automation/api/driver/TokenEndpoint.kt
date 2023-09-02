package uk.co.baconi.oauth.automation.api.driver

import com.typesafe.config.Config
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import org.hamcrest.Matchers.equalTo
import uk.co.baconi.oauth.automation.api.config.Client
import uk.co.baconi.oauth.automation.api.config.ConfidentialClient
import uk.co.baconi.oauth.automation.api.config.PublicClient
import uk.co.baconi.oauth.automation.api.getUri
import uk.co.baconi.oauth.automation.api.isUUID
import java.net.URI

interface TokenEndpoint {

    val config: Config
    val serverSpecification: RequestSpecification

    val tokenLocation: URI
        get() = config.getUri("token.location")

    fun authorisationCodeGrant(client: ConfidentialClient, code: String): ValidatableResponse {

        return given(serverSpecification)
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
            .contentType(ContentType.JSON)
            .body(
                "access_token", isUUID(),
                "refresh_token", isUUID(),
                "token_type", equalTo("bearer"),
                "expires_in", equalTo(7200)
            )
            .statusCode(200)
    }

    fun authorisationCodeGrant(client: Client, code: String, codeVerifier: String): ValidatableResponse {

        return given(serverSpecification)
            .withConfidentialAuthentication(client)
            .contentType(ContentType.URLENC)
            .formParams(
                buildMap<String, String> {
                    this["grant_type"] = "authorization_code"
                    this["redirect_uri"] = client.redirectUri.toASCIIString()
                    this["code"] = code
                    this["code_verifier"] = codeVerifier
                    withPublicAuthentication(client)
                }
            )
            .post(tokenLocation)
            .then()
            .contentType(ContentType.JSON)
            .body(
                "access_token", isUUID(),
                "refresh_token", isUUID(),
                "token_type", equalTo("bearer"),
                "expires_in", equalTo(7200)
            )
            .statusCode(200)
    }

    private fun RequestSpecification.withConfidentialAuthentication(client: Client) = when (client) {
        is PublicClient -> this // No authentication headers, they go in the body
        is ConfidentialClient -> auth().preemptive().basic(client)
    }

    private fun MutableMap<String, String>.withPublicAuthentication(client: Client) {
        if (client is PublicClient) this["client_id"] = client.id.value
    }
}
