package uk.co.baconi.oauth.automation.api.driver

import com.typesafe.config.Config
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.internal.csrf.CsrfTokenFinder
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import org.hamcrest.Matchers.equalTo
import uk.co.baconi.oauth.automation.api.config.Client
import uk.co.baconi.oauth.automation.api.config.User
import uk.co.baconi.oauth.automation.api.getUri
import java.net.URI
import java.util.*

interface AuthenticationEndpoint {

    val config: Config
    val browserSpecification: RequestSpecification

    val authenticationLocation: URI
        get() = config.getUri("authentication.location")

    fun authenticate(user: User, csrfToken: String) {

        given(browserSpecification)
            .contentType(ContentType.JSON)
            .body("""{"username": "${user.username}", "password": ${user.password.contentToString()}, "csrfToken": "$csrfToken"}""")
            .post(authenticationLocation)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("type", equalTo("success"))
            .body("username", equalTo(user.username))
    }
}