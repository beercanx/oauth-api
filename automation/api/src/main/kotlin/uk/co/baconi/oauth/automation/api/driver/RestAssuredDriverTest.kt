package uk.co.baconi.oauth.automation.api.driver

import io.restassured.config.CsrfConfig.csrfConfig
import io.restassured.internal.csrf.CsrfTokenFinder.findInHtml
import io.restassured.response.Response
import io.restassured.response.ValidatableResponse
import org.junit.jupiter.api.extension.ExtendWith
import uk.co.baconi.oauth.automation.api.config.AccessToken
import uk.co.baconi.oauth.automation.api.config.ClientSourceResolver
import uk.co.baconi.oauth.automation.api.config.ConfidentialClientResolver
import uk.co.baconi.oauth.automation.api.config.UserResolver
import java.net.URI

@ExtendWith(UserResolver::class)
@ExtendWith(ClientSourceResolver::class)
@ExtendWith(ConfidentialClientResolver::class)
abstract class RestAssuredDriverTest {

    protected val driver: RestAssuredDriver = RestAssuredDriver()

    protected fun ValidatableResponse.extractAccessToken(): AccessToken {
        return extract()
            .body()
            .jsonPath()
            .getString("access_token")
            .let(::AccessToken)
    }

    protected fun ValidatableResponse.extractLocationsQueryParameters(): Map<String, String> {
        return extract()
            .header("Location")
            .let(URI::create)
            .query
            .split("&")
            .map { it.split("=", limit = 2) }
            .associate { it[0] to it[1] }
    }

    protected fun Response.extractCsrfToken(): String {
        return checkNotNull(findInHtml(csrfConfig().csrfMetaTagName("_csrf"), this)?.token) {
            "CSRF token not found"
        }
    }
}