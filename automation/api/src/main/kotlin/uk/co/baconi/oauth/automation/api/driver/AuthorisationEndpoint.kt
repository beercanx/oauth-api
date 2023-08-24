package uk.co.baconi.oauth.automation.api.driver

import com.typesafe.config.Config
import io.restassured.RestAssured.given
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification
import uk.co.baconi.oauth.automation.api.config.Client
import uk.co.baconi.oauth.automation.api.getUri
import java.net.URI
import java.util.*

interface AuthorisationEndpoint {

    val config: Config
    val browserSpecification: RequestSpecification

    val authorisationLocation: URI
        get() = config.getUri("authorisation.location")

    fun authorise(client: Client, state: UUID, scopes: Set<String>, expectedStatus: Int): Response {

        return given(browserSpecification)
            .queryParams(
                mapOf(
                    "response_type" to "code",
                    "client_id" to client.id.value,
                    "redirect_uri" to client.redirectUri.toASCIIString(),
                    "state" to state,
                    "scope" to scopes.joinToString(separator = " "),
                )
            )
            .get(authorisationLocation)
            .apply {
                then().statusCode(expectedStatus)
            }
    }
}