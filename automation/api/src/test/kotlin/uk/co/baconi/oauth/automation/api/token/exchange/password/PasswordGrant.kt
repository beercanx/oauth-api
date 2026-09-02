package uk.co.baconi.oauth.automation.api.token.exchange.password

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Tag
import org.junit.jupiter.params.ParameterizedTest
import uk.co.baconi.oauth.automation.api.AUTOMATION
import uk.co.baconi.oauth.automation.api.config.ClientSource
import uk.co.baconi.oauth.automation.api.config.ClientType
import uk.co.baconi.oauth.automation.api.config.ConfidentialClient
import uk.co.baconi.oauth.automation.api.config.GrantType
import uk.co.baconi.oauth.automation.api.config.PublicClient
import uk.co.baconi.oauth.automation.api.config.User
import uk.co.baconi.oauth.automation.api.config.UserState
import uk.co.baconi.oauth.automation.api.config.UserType
import uk.co.baconi.oauth.automation.api.driver.RestAssuredDriverTest
import uk.co.baconi.oauth.automation.api.driver.withConfidentialAuthentication
import uk.co.baconi.oauth.automation.api.driver.withPublicAuthentication
import uk.co.baconi.oauth.automation.api.isUUID
import kotlin.collections.buildMap
import kotlin.collections.set

@Tag(AUTOMATION)
class PasswordGrant : RestAssuredDriverTest() {

    @ParameterizedTest
    @UserType(state = UserState.Active)
    fun `should be able to password grant for each active test user`(user: User, client: ConfidentialClient) {
        driver.passwordGrant(client, user, setOf("basic"))
    }

    @ParameterizedTest
    @ClientSource(clientTypes = [ClientType.Confidential], grantTypes = [GrantType.Password])
    fun `should be able to password grant for each client with it configured`(client: ConfidentialClient, user: User) {
        driver.passwordGrant(client, user, setOf("basic"))
    }

    @ParameterizedTest
    @ClientSource(clientTypes = [ClientType.Public])
    fun `should NOT be able to password grant for public clients`(client: PublicClient, user: User) {
        given(driver.serverSpecification)
            .contentType(ContentType.URLENC)
            .formParams(
                buildMap {
                    this["grant_type"] = "password"
                    this["username"] = user.username
                    this["password"] = user.password
                    this["scope"] = "basic"
                    withPublicAuthentication(client)
                }
            )
            .post(driver.tokenLocation)
            .then()
            .contentType(ContentType.JSON)
            .body(
                "error", equalTo("unauthorized_client"),
                "error_description", equalTo("not authorized to: password")
            )
            .statusCode(400)
    }

    @ParameterizedTest
    @UserType(state = UserState.Closed)
    fun `should NOT be able to password grant for any closed test user`(user: User, client: ConfidentialClient) {
        expectUserAuthenticationToFail(user, client, "Closed")
    }

    @ParameterizedTest
    @UserType(state = UserState.Suspended)
    fun `should NOT be able to password grant for any suspended test user`(user: User, client: ConfidentialClient) {
        expectUserAuthenticationToFail(user, client, "Suspended")
    }

    @ParameterizedTest
    @UserType(state = UserState.Locked)
    fun `should NOT be able to password grant for any locked test user`(user: User, client: ConfidentialClient) {
        expectUserAuthenticationToFail(user, client, "Locked")
    }

    private fun expectUserAuthenticationToFail(user: User, client: ConfidentialClient, expectedReason: String) {

        given(driver.serverSpecification)
            .withConfidentialAuthentication(client)
            .contentType(ContentType.URLENC)
            .formParams(
                mapOf(
                    "grant_type" to "password",
                    "username" to user.username,
                    "password" to user.password,
                    "scope" to "basic"
                )
            )
            .post(driver.tokenLocation)
            .then()
            .contentType(ContentType.JSON)
            .body(
                "error", equalTo("invalid_grant"),
                "error_description", equalTo(expectedReason)
            )
            .statusCode(400)
    }
}