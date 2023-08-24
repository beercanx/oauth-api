package uk.co.baconi.oauth.automation.api.driver

import io.restassured.specification.AuthenticationSpecification
import io.restassured.specification.PreemptiveAuthSpec
import io.restassured.specification.RequestSpecification
import uk.co.baconi.oauth.automation.api.config.Client
import uk.co.baconi.oauth.automation.api.config.ClientId
import uk.co.baconi.oauth.automation.api.config.ClientSecret

fun PreemptiveAuthSpec.basic(client: Client): RequestSpecification {
    return basic(client.id, client.secret)
}

fun PreemptiveAuthSpec.basic(clientId: ClientId, clientSecret: ClientSecret): RequestSpecification {
    return basic(clientId.value, clientSecret.value)
}

fun AuthenticationSpecification.basic(client: Client): RequestSpecification {
    return basic(client.id, client.secret)
}

fun AuthenticationSpecification.basic(clientId: ClientId, clientSecret: ClientSecret): RequestSpecification {
    return basic(clientId.value, clientSecret.value)
}