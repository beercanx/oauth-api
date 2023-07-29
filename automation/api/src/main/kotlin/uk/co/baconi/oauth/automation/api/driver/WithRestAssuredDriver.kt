package uk.co.baconi.oauth.automation.api.driver

interface WithRestAssuredDriver {

    val driver: RestAssuredDriver
        get() = RestAssuredDriver()

}