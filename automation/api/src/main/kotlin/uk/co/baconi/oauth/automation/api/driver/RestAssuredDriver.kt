package uk.co.baconi.oauth.automation.api.driver

import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.cookie.CookieFilter
import io.restassured.specification.RequestSpecification
import org.slf4j.LoggerFactory

class RestAssuredDriver {

    companion object {
        private val logger = LoggerFactory.getLogger(RestAssuredDriver::class.java)
    }

    private val cookieFilter = CookieFilter()

    /**
     * The [RequestSpecification] for performing browser requests.
     */
    val browser: RequestSpecification = RequestSpecBuilder()
        .addFilter(cookieFilter)
        .build()

    /**
     * The [RequestSpecification] for performing rest requests.
     */
    val rest: RequestSpecification = RequestSpecBuilder()
        .build()

    init {
        logger.trace("Initialised RestAssuredDriver")
    }
}
