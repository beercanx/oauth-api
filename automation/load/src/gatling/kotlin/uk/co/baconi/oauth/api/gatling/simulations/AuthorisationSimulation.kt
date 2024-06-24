package uk.co.baconi.oauth.api.gatling.simulations

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.*
import io.gatling.javaapi.http.HttpDsl.http
import uk.co.baconi.oauth.api.gatling.endpoints.AuthenticationEndpoint.Operations.authenticate
import uk.co.baconi.oauth.api.gatling.endpoints.IntrospectionEndpoint.Operations.introspectAccessToken
import uk.co.baconi.oauth.api.gatling.endpoints.TokenEndpoint.Operations.authorisationCodeGrant
import uk.co.baconi.oauth.api.gatling.endpoints.AuthorisationEndpoint.Operations.authoriseWithAuthenticationPage
import uk.co.baconi.oauth.api.gatling.endpoints.AuthorisationEndpoint.Operations.authoriseWithAuthorisationCode
import uk.co.baconi.oauth.api.gatling.feeders.Clients.Setup.withClient
import uk.co.baconi.oauth.api.gatling.feeders.Customers.Feeders.customers
import uk.co.baconi.oauth.api.gatling.feeders.State.Setup.withState

class AuthorisationSimulation : Simulation() {

    private val theScenario = scenario("Authorisation")
        .exec(withClient("consumer-z", "7XLlyzjRpvICEkNrsgtOuuj1S30Bj9Xu", "https://consumer-z.baconi.co.uk/callback")) // TODO - Extract into environmental config
        .exec(withState())
        .feed(arrayFeeder(customers).circular())
        .exec(authoriseWithAuthenticationPage)
        .exitHereIfFailed()
        .exec(authenticate)
        .exitHereIfFailed()
        .exec(authoriseWithAuthorisationCode)
        .exitHereIfFailed()
        .exec(authorisationCodeGrant)
        .exitHereIfFailed()
        .exec(introspectAccessToken)
        .exitHereIfFailed()

    private val httpProtocol = http
        .baseUrl("http://localhost:8080") // TODO - Extract into environmental config
        .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0") // TODO - Use various types

    init {
        setUp(theScenario.injectOpen(atOnceUsers(1)).protocols(httpProtocol))
            .assertions(forAll().failedRequests().count().shouldBe(0)) // Cause Gradle output to error on any failure.
    }
}