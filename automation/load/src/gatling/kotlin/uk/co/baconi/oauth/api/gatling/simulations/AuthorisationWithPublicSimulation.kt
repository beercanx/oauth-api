package uk.co.baconi.oauth.api.gatling.simulations

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.*
import io.gatling.javaapi.http.HttpDsl.http
import uk.co.baconi.oauth.api.gatling.endpoints.AuthenticationEndpoint.Operations.authenticate
import uk.co.baconi.oauth.api.gatling.endpoints.IntrospectionEndpoint.Operations.introspectAccessToken
import uk.co.baconi.oauth.api.gatling.endpoints.TokenEndpoint.Operations.authorisationCodeGrant
import uk.co.baconi.oauth.api.gatling.endpoints.AuthorisationEndpoint.Operations.confidentialAuthorisationWithPage
import uk.co.baconi.oauth.api.gatling.endpoints.AuthorisationEndpoint.Operations.confidentialAuthorisationWithCode
import uk.co.baconi.oauth.api.gatling.feeders.Clients.Client.Type.Confidential
import uk.co.baconi.oauth.api.gatling.feeders.Clients.Client.Type.Public
import uk.co.baconi.oauth.api.gatling.feeders.Clients.Setup.withPublicClient
import uk.co.baconi.oauth.api.gatling.feeders.Customers.Feeders.customers
import uk.co.baconi.oauth.api.gatling.feeders.ProofOfKeyCodeExchange.Setup.generateVerifierAndChallenge
import uk.co.baconi.oauth.api.gatling.feeders.State.Setup.generateState

class AuthorisationWithPublicSimulation : Simulation() {

    private val theScenario = scenario("Authorisation Code Grant with Public Client")
        .exec(withPublicClient("consumer-y", "uk.co.baconi.consumer-y://callback")) // TODO - Extract into environmental config
        .feed(arrayFeeder(customers).circular())
        .exec(generateState())
        .exec(generateVerifierAndChallenge())
        .exec(confidentialAuthorisationWithPage(Public))
        .exitHereIfFailed()
        .exec(authenticate)
        .exitHereIfFailed()
        .exec(confidentialAuthorisationWithCode(Public))
        .exitHereIfFailed()
        .exec(authorisationCodeGrant(Public))
        .exitHereIfFailed()

    private val httpProtocol = http
        .baseUrl("http://localhost:8080") // TODO - Extract into environmental config
        .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0") // TODO - Use various types

    init {
        setUp(theScenario.injectOpen(atOnceUsers(1)).protocols(httpProtocol))
            .assertions(forAll().failedRequests().count().shouldBe(0)) // Cause Gradle output to error on any failure.
    }
}