package uk.co.baconi.oauth.automation.api.driver

import com.typesafe.config.Config
import io.restassured.RestAssured.given
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification
import uk.co.baconi.oauth.automation.api.RFC7636
import uk.co.baconi.oauth.automation.api.config.Client
import uk.co.baconi.oauth.automation.api.config.ClientType.Confidential
import uk.co.baconi.oauth.automation.api.config.ClientType.Public
import uk.co.baconi.oauth.automation.api.config.ConfidentialClient
import uk.co.baconi.oauth.automation.api.getUri
import java.net.URI
import java.util.*

interface AuthorisationEndpoint {

    val config: Config
    val browserSpecification: RequestSpecification

    val authorisationLocation: URI
        get() = config.getUri("authorisation.location")

    /**
     * Performs an authorise request for the authorisation code grant.
     *
     * That can only be used by [Confidential] clients.
     */
    fun authorise(client: ConfidentialClient, state: UUID, scopes: Set<String>, expectedStatus: Int): Response {

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

    /**
     * Performs an authorise request for the authorisation code grant with the Proof Key of Code Exchange [RFC7636].
     *
     * That can be used by both [Public] and [Confidential] clients.
     */
    fun authorise(
        client: Client,
        state: UUID,
        scopes: Set<String>,
        codeChallenge: String,
        codeChallengeMethod: String,
        expectedStatus: Int
    ): Response {

        return given(browserSpecification)
            .queryParams(
                mapOf(
                    "response_type" to "code",
                    "client_id" to client.id.value,
                    "redirect_uri" to client.redirectUri.toASCIIString(),
                    "state" to state,
                    "scope" to scopes.joinToString(separator = " "),
                    "code_challenge" to codeChallenge,
                    "code_challenge_method" to codeChallengeMethod
                )
            )
            .get(authorisationLocation)
            .apply {
                then().statusCode(expectedStatus)
            }
    }
}
