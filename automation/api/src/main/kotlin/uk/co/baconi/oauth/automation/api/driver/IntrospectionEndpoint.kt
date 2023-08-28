package uk.co.baconi.oauth.automation.api.driver

import com.typesafe.config.Config
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import uk.co.baconi.oauth.automation.api.config.AccessToken
import uk.co.baconi.oauth.automation.api.config.Client
import uk.co.baconi.oauth.automation.api.config.ConfidentialClient
import uk.co.baconi.oauth.automation.api.getUri
import java.net.URI

interface IntrospectionEndpoint {

    val config: Config
    val serverSpecification: RequestSpecification

    val introspectionLocation: URI
        get() = config.getUri("token.introspection.location")

    /**
     * Perform an introspection as [client] of [accessToken]
     */
    fun introspect(client: ConfidentialClient, accessToken: AccessToken): ValidatableResponse {
        return given(serverSpecification)
            .auth().preemptive().basic(client)
            .contentType(ContentType.URLENC)
            .formParams(
                mapOf(
                    "token" to accessToken.value
                )
            )
            .post(introspectionLocation)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
    }
}