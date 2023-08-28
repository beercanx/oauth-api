package uk.co.baconi.oauth.automation.api.driver

import com.typesafe.config.Config
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import org.hamcrest.Matchers.equalTo
import uk.co.baconi.oauth.automation.api.config.User
import uk.co.baconi.oauth.automation.api.getUri
import java.net.URI

interface AuthenticationEndpoint {

    val config: Config
    val browserSpecification: RequestSpecification

    val authenticationLocation: URI
        get() = config.getUri("authentication.location")

    fun authenticate(user: User, csrfToken: String) {

        val (username, password) = user

        given(browserSpecification)
            .contentType(ContentType.JSON)
            .body("""{"username": "$username", "password": ${password.asJsonArray()}, "csrfToken": "$csrfToken"}""")
            .post(authenticationLocation)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("type", equalTo("success"))
            .body("username", equalTo(user.username))
    }

    private fun String.asJsonArray() = toCharArray().contentToString()

}