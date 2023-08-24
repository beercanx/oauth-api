package uk.co.baconi.oauth.automation.api.driver

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.restassured.builder.RequestSpecBuilder
import io.restassured.config.CsrfConfig
import io.restassured.config.CsrfConfig.csrfConfig
import io.restassured.config.RedirectConfig
import io.restassured.config.RedirectConfig.redirectConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.config.RestAssuredConfig.config
import io.restassured.filter.cookie.CookieFilter
import io.restassured.specification.RequestSpecification
import org.slf4j.LoggerFactory

class RestAssuredDriver(override val config: Config) : AuthenticationEndpoint, AuthorisationEndpoint, IntrospectionEndpoint, TokenEndpoint {

    constructor() : this(ConfigFactory.load().getConfig("uk.co.baconi.oauth.automation.api"))

    companion object {
        private val logger = LoggerFactory.getLogger(RestAssuredDriver::class.java)
    }

    private val cookieFilter = CookieFilter()

    private val baseSpecification = RequestSpecBuilder()
        .setConfig(config()
            .redirect(redirectConfig().followRedirects(false))
        )
        .build()

    /**
     * The [RequestSpecification] for performing browser to server requests.
     */
    override val browserSpecification: RequestSpecification = RequestSpecBuilder()
        .addRequestSpecification(baseSpecification)
        .addFilter(cookieFilter)
        .build()

    /**
     * The [RequestSpecification] for performing server to server requests.
     */
    override val serverSpecification: RequestSpecification = RequestSpecBuilder()
        .addRequestSpecification(baseSpecification)
        .build()

    init {
        logger.trace("Initialised RestAssuredDriver")
    }
}
