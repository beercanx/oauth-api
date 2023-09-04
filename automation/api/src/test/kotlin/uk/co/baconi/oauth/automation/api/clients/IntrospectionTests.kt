package uk.co.baconi.oauth.automation.api.clients

import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import uk.co.baconi.oauth.automation.api.AUTOMATION
import uk.co.baconi.oauth.automation.api.CLIENTS
import uk.co.baconi.oauth.automation.api.config.*
import uk.co.baconi.oauth.automation.api.config.ClientCapabilities.Introspection
import uk.co.baconi.oauth.automation.api.config.ClientType.Confidential
import uk.co.baconi.oauth.automation.api.config.GrantType.Password
import uk.co.baconi.oauth.automation.api.driver.RestAssuredDriverTest

@Tag(CLIENTS)
@Tag(AUTOMATION)
class IntrospectionTests : RestAssuredDriverTest() {

    companion object : RestAssuredDriverTest() {

        private lateinit var issuedClient: ClientId
        private lateinit var username: String

        private lateinit var accessToken: AccessToken
        private lateinit var refreshToken: RefreshToken

        @BeforeAll
        @JvmStatic
        fun createTokens(@ClientSource([Confidential], [Password]) client: ConfidentialClient, user: User) {
            issuedClient = client.id
            username = user.username

            val response = driver.passwordGrant(client, user, setOf("basic"))

            accessToken = response.extractAccessToken()
            refreshToken = response.extractRefreshToken()
        }
    }

    @ParameterizedTest
    @ClientSource(clientTypes = [Confidential], capabilities = [Introspection])
    fun `introspect access token`(client: ConfidentialClient) {

        driver.introspect(client, accessToken)
            .body(
                "active", equalTo(true),
                "scope", equalTo("basic"),
                "client_id", equalTo(issuedClient.value),
                "sub", equalTo(username),
                "username", equalTo(username),
            )
    }

    @ParameterizedTest
    @ClientSource(clientTypes = [Confidential], capabilities = [Introspection])
    fun `introspect refresh token`(client: ConfidentialClient) {

        driver.introspect(client, refreshToken)
            .body("active", equalTo(false))
    }
}