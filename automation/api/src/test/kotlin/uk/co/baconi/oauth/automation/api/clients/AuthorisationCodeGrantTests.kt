package uk.co.baconi.oauth.automation.api.clients

import io.kotest.matchers.shouldBe
import io.restassured.response.ValidatableResponse
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import uk.co.baconi.oauth.automation.api.AUTOMATION
import uk.co.baconi.oauth.automation.api.CLIENTS
import uk.co.baconi.oauth.automation.api.config.*
import uk.co.baconi.oauth.automation.api.config.ClientType.Confidential
import uk.co.baconi.oauth.automation.api.driver.RestAssuredDriverTest
import java.util.*

@Tag(CLIENTS)
@Tag(AUTOMATION)
class AuthorisationCodeGrantTests : RestAssuredDriverTest() {

    @ParameterizedTest
    @ClientSource(clientTypes = [Confidential])
    fun `authorisation code grant`(client: ConfidentialClient, user: User) {

        val state = UUID.randomUUID()
        val scopes = setOf("basic")

        val csrfToken = driver.authorise(client, state, scopes, expectedStatus = 200)
            .extractCsrfToken()

        driver.authenticate(user, csrfToken)

        val queryParameters = driver.authorise(client, state, scopes, expectedStatus = 302)
            .then()
            .header("Location", startsWith(client.redirectUri.toASCIIString()))
            .extractLocationsQueryParameters()

        val code = checkNotNull(queryParameters["code"]) {
            "code should not be null in queryParameters $queryParameters"
        }

        checkNotNull(queryParameters["state"]) {
            "state should not be null in queryParameters $queryParameters"
        } shouldBe state.toString()

        val accessToken = driver.authorisationCodeGrant(client, code)
            .body("scope", equalTo("basic"))
            .body("state", equalTo(state.toString()))
            .extractAccessToken()

        driver.introspect(client, accessToken)
            .body("active", equalTo(true))
            .body("scope", equalTo("basic"))
            .body("client_id", equalTo(client.id.value))
            .body("sub", equalTo(user.username))
            .body("username", equalTo(user.username))
            .body("token_type", equalTo("bearer"))
            .body("exp", notNullValue())
            .body("iat", notNullValue())
            .body("nbf", notNullValue())
    }

    //@ParameterizedTest
    //@ClientSource(with = [ProofKeyOfCodeExchange])
    fun `authorisation code grant using PKCE`(client: Client, user: User) {

        // TODO - Do stuff before this
        val code = ""

        val response: ValidatableResponse = when(client) {
            is PublicClient -> TODO("driver.authorisationCodeGrant(client, code, challenge..etc)")
            is ConfidentialClient -> driver.authorisationCodeGrant(client, code)
        }

        TODO("Implement")
    }
}