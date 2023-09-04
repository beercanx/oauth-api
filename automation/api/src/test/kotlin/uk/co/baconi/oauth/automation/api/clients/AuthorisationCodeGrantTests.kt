package uk.co.baconi.oauth.automation.api.clients

import io.kotest.matchers.shouldBe
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import uk.co.baconi.oauth.automation.api.AUTOMATION
import uk.co.baconi.oauth.automation.api.CLIENTS
import uk.co.baconi.oauth.automation.api.config.Client
import uk.co.baconi.oauth.automation.api.config.ClientCapabilities.ProofKeyOfCodeExchange
import uk.co.baconi.oauth.automation.api.config.ClientSource
import uk.co.baconi.oauth.automation.api.config.ClientType.Confidential
import uk.co.baconi.oauth.automation.api.config.ClientType.Public
import uk.co.baconi.oauth.automation.api.config.ConfidentialClient
import uk.co.baconi.oauth.automation.api.config.GrantType.AuthorizationCode
import uk.co.baconi.oauth.automation.api.config.User
import uk.co.baconi.oauth.automation.api.driver.RestAssuredDriverTest
import java.util.*

@Tag(CLIENTS)
@Tag(AUTOMATION)
class AuthorisationCodeGrantTests : RestAssuredDriverTest() {

    @ParameterizedTest
    @ClientSource(clientTypes = [Confidential], grantTypes = [AuthorizationCode])
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
            .body(
                "scope", equalTo("basic"),
                "state", equalTo(state.toString())
            )
            .extractAccessToken()

        driver.introspect(client, accessToken)
            .body(
                "active", equalTo(true),
                "scope", equalTo("basic"),
                "client_id", equalTo(client.id.value),
                "sub", equalTo(user.username),
                "username", equalTo(user.username),
                "token_type", equalTo("bearer"),
                "exp", notNullValue(),
                "iat", notNullValue(),
                "nbf", notNullValue()
            )
    }

    @ParameterizedTest
    @ClientSource([Public, Confidential], grantTypes = [AuthorizationCode], capabilities = [ProofKeyOfCodeExchange])
    fun `authorisation code grant using PKCE`(client: Client, user: User) {

        val state = UUID.randomUUID()
        val scopes = setOf("basic")
        val codeChallengeMethod = "S256"
        val codeVerifier = "skZ-Ma4wP_IR8o9M2U4wMMY5gEhvYg4-j6Il8g99GFMRcqvE"
        val codeChallenge = "u7a9y9LFsPSeFohPRcPPGxt8oGYaJE16qHH7H7eM9K0"

        val csrfToken = driver.authorise(client, state, scopes, codeChallenge, codeChallengeMethod, 200)
            .extractCsrfToken()

        driver.authenticate(user, csrfToken)

        val queryParameters = driver.authorise(client, state, scopes, codeChallenge, codeChallengeMethod, 302)
            .then()
            .header("Location", startsWith(client.redirectUri.toASCIIString()))
            .extractLocationsQueryParameters()

        val code = checkNotNull(queryParameters["code"]) {
            "code should not be null in queryParameters $queryParameters"
        }

        checkNotNull(queryParameters["state"]) {
            "state should not be null in queryParameters $queryParameters"
        } shouldBe state.toString()

        driver.authorisationCodeGrant(client, code, codeVerifier)
            .body(
                "scope", equalTo("basic"),
                "state", equalTo(state.toString())
            )
    }
}