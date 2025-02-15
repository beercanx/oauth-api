package uk.co.baconi.oauth.automation.browser.confidential

import com.codeborne.selenide.Selectors.byId
import com.codeborne.selenide.Selectors.byName
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.Condition.*
import com.codeborne.selenide.Configuration
import com.codeborne.selenide.WebDriverConditions.*
import com.codeborne.selenide.Selenide.*
import com.typesafe.config.ConfigFactory
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.beInstanceOf
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.openqa.selenium.Cookie
import org.openqa.selenium.WebDriverException
import org.slf4j.LoggerFactory
import uk.co.baconi.oauth.automation.browser.AUTOMATION
import java.net.URI
import java.net.URL
import java.net.URLDecoder
import java.net.URLDecoder.decode
import java.net.URLEncoder.encode
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.*
import java.time.temporal.TemporalUnit
import java.util.*
import kotlin.text.Charsets.UTF_8

@Tag(AUTOMATION)
class ConfidentialAuthorisationCodeGrantTest {

    private val config = ConfigFactory.load().getConfig("uk.co.baconi.oauth.automation.browser")
    private val authorisationLocation = config.getString("authorisation.location")

    // TODO - Move into config or a test user manager.
    private val username = "aardvark"
    private val password = "121212"

    @AfterEach
    fun clearBrowser() {
        closeWebDriver()
    }

    @Test
    fun `authorisation code grant when not authenticated`() {

        val state = UUID.randomUUID().toString()

        // Starting an authorise request we are shown the login page
        open(buildUrl(authorisationLocation, parameters = listOf(
            "response_type" to "code",
            "client_id" to "consumer-z",
            "redirect_uri" to "https://consumer-z.baconi.co.uk/callback",
            "state" to state,
            "scope" to "basic",
        )))

        // fill in username, password and click login
        element(byId("login-form")).apply {
            find(byName("username")).setValue(username)
            find(byName("password")).setValue(password)
            find(byName("login")).click()
        }

        // The callback uri is called with a code and the original state value
        val callbackUri = webdriver()
            .shouldHave(urlStartingWith("https://consumer-z.baconi.co.uk/callback"))
            .shouldHave(urlContaining("code="))
            .shouldHave(urlContaining("state=$state"))
            .driver().url()
            .asUrl()

        // Verify the callback is a successful type
        assertSoftly(callbackUri.extractQueryParameters()) {
            this.shouldNotBeNull()
            this shouldContainKey "state"
            this shouldContain ("state" to state)
            this shouldContainKey "code"
            this["code"]?.let(UUID::fromString) should beInstanceOf<UUID>() // A crude and terrible assertion
        }

        // TODO - Utilise a test app to prove the authentication code is valid and exchangeable.
    }


    @Test
    fun `authorisation code grant when already authenticated`() {

        // Setup being logged in...
        `authorisation code grant when not authenticated`()

        val state = UUID.randomUUID().toString()

        // Starting an authorise request
        try {
            open(buildUrl(authorisationLocation, parameters = listOf(
                "response_type" to "code",
                "client_id" to "consumer-z",
                "redirect_uri" to "https://consumer-z.baconi.co.uk/callback",
                "state" to state,
                "scope" to "basic",
            )))
        } catch (exception: WebDriverException) {
            // Ignoring exceptions because we get a direct redirect back to the redirect uri, which cannot be resolved.
            exception.message shouldContain "unknown error: net::ERR_NAME_NOT_RESOLVED"
        }

        // The callback uri is called with a code and the original state value
        val callbackUri = webdriver()
            .shouldHave(urlStartingWith("https://consumer-z.baconi.co.uk/callback"))
            .driver()
            .url()
            .asUrl()

        // Verify the callback is a successful type
        assertSoftly(callbackUri.extractQueryParameters()) { parameters ->
            parameters shouldContainKey "state"
            parameters shouldContain ("state" to state)
            parameters shouldContainKey "code"
            parameters["code"]?.let(UUID::fromString) should beInstanceOf<UUID>() // A crude and terrible assertion
        }

        // TODO - Utilise a test app to prove the authentication code is valid and exchangeable.
    }

    @Test
    fun `authorisation code grant should allow users to abort`() {

        val state = UUID.randomUUID().toString()

        // Starting an authorise request we are shown the login page
        open(buildUrl(authorisationLocation, parameters = listOf(
            "response_type" to "code",
            "client_id" to "consumer-z",
            "redirect_uri" to "https://consumer-z.baconi.co.uk/callback",
            "state" to state,
            "scope" to "basic",
        )))

        element(byId("login-form"))
            .find(byName("abort"))
            .click()

        // The callback uri is called with an error and description
        val callbackUri = webdriver()
            .shouldHave(urlStartingWith("https://consumer-z.baconi.co.uk/callback"))
            .driver()
            .url()
            .asUrl()

        assertSoftly(callbackUri.extractQueryParameters()) { parameters ->
            parameters shouldContain ("error" to "access_denied")
            parameters shouldContain ("error_description" to "user aborted")
            parameters shouldContain ("state" to state)
        }
    }

    private fun buildUrl(base: String, parameters: List<Pair<String, String>> = emptyList()): URL {
        return URI(buildString {
            append(base)
            if (parameters.isNotEmpty()) {
                append('?')
                append(parameters.joinToString("&") { (key, value) ->
                    "$key=${encode(value, UTF_8)}"
                })
            }
        }).toURL()
    }

    private fun String.asUrl() = URI(this).toURL()

    private fun URL.extractQueryParameters(): Map<String, String> {
        val query = query?.split("&")?.associate { it.split("=").let { (key, value) -> key to decode(value, UTF_8) } }
        query.shouldNotBeNull()
        return query
    }
}