package uk.co.baconi.oauth.automation.api.driver

import io.restassured.specification.AuthenticationSpecification
import io.restassured.specification.PreemptiveAuthSpec
import io.restassured.specification.RequestSpecification
import uk.co.baconi.oauth.automation.api.config.ClientId
import uk.co.baconi.oauth.automation.api.config.ClientSecret
import uk.co.baconi.oauth.automation.api.config.ConfidentialClient

fun AuthenticationSpecification.basic(client: ConfidentialClient): RequestSpecification {
    return preemptive().basic(client)
}

fun PreemptiveAuthSpec.basic(client: ConfidentialClient): RequestSpecification {
    return basic(client.id, client.secret)
}

fun PreemptiveAuthSpec.basic(clientId: ClientId, clientSecret: ClientSecret): RequestSpecification {
    return basic(clientId.value, clientSecret.value)
}