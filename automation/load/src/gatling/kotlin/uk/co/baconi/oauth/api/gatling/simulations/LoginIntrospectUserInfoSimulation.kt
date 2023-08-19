package uk.co.baconi.oauth.api.gatling.simulations

import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.*
import io.gatling.javaapi.http.HttpDsl.http
import uk.co.baconi.oauth.api.gatling.endpoints.IntrospectionEndpoint.Operations.introspectAccessToken
import uk.co.baconi.oauth.api.gatling.endpoints.TokenEndpoint.Operations.passwordCredentialsGrant
import uk.co.baconi.oauth.api.gatling.endpoints.UserInfoEndpoint.Operations.userInfoWithAccessToken
import uk.co.baconi.oauth.api.gatling.feeders.Clients.Setup.withClient
import uk.co.baconi.oauth.api.gatling.feeders.Customers.Feeders.customers

class LoginIntrospectUserInfoSimulation : Simulation() {

    private val theScenario = scenario("Login, Introspect and get User Info")
        .exec(withClient("consumer-z", "7XLlyzjRpvICEkNrsgtOuuj1S30Bj9Xu")) // TODO - Extract into environmental config
        .feed(arrayFeeder(customers).circular())
        .exec(passwordCredentialsGrant)
        .exitHereIfFailed()
        .exec(introspectAccessToken)
        .exitHereIfFailed()
        .exec(userInfoWithAccessToken)
        .exitHereIfFailed()

    private val httpProtocol = http
        .baseUrl("http://localhost:8080") // TODO - Extract into environmental config
        .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0") // TODO - Use various types

    init {
        setUp(theScenario.injectOpen(atOnceUsers(1)).protocols(httpProtocol))
    }
}