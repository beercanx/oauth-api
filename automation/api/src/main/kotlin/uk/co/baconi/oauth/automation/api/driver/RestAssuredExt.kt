package uk.co.baconi.oauth.automation.api.driver

import io.restassured.specification.AuthenticationSpecification
import io.restassured.specification.PreemptiveAuthSpec
import io.restassured.specification.RequestSpecification
import uk.co.baconi.oauth.automation.api.config.Client
import uk.co.baconi.oauth.automation.api.config.ClientId
import uk.co.baconi.oauth.automation.api.config.ClientSecret
import uk.co.baconi.oauth.automation.api.config.ConfidentialClient
import uk.co.baconi.oauth.automation.api.config.PublicClient

fun AuthenticationSpecification.basic(client: ConfidentialClient): RequestSpecification {
    return preemptive().basic(client)
}

fun PreemptiveAuthSpec.basic(client: ConfidentialClient): RequestSpecification {
    return basic(client.id, client.secret)
}

fun PreemptiveAuthSpec.basic(clientId: ClientId, clientSecret: ClientSecret): RequestSpecification {
    return basic(clientId.value, clientSecret.value)
}

fun RequestSpecification.withConfidentialAuthentication(client: Client) = when (client) {
    is PublicClient -> this // No authentication headers, they go in the body
    is ConfidentialClient -> auth().preemptive().basic(client)
}

fun MutableMap<String, Any?>.withPublicAuthentication(client: Client) {
    if (client is PublicClient) this["client_id"] = client.id.value
}
