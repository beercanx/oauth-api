package uk.co.baconi.oauth.api.scenarios

import uk.co.baconi.oauth.api.endpoints.Introspection.Operations.introspectAccessToken
import uk.co.baconi.oauth.api.endpoints.TokenExchange.Operations.passwordFlow
import uk.co.baconi.oauth.api.endpoints.UserInfo.Operations.userInfoWithAccessToken
import uk.co.baconi.oauth.api.feeders.Clients.Setup.withClient
import uk.co.baconi.oauth.api.feeders.Customers.Feeders.customers
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class LoginIntrospectUserInfoSimulation extends Simulation {

  private val scn = scenario("Login, Introspect and get User Info")
    .exec(withClient("consumer-z", "7XLlyzjRpvICEkNrsgtOuuj1S30Bj9Xu")) // TODO - Extract into environmental config
    .feed(customers.circular)
    .exec(passwordFlow)
    .exitHereIfFailed
    .exec(introspectAccessToken)
    .exitHereIfFailed
    .exec(userInfoWithAccessToken)
    .exitHereIfFailed

  private val httpProtocol = http
    .baseUrl("http://localhost:8080") // TODO - Extract into environmental config
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0") // TODO - Use various types

  setUp(scn.inject(constantUsersPerSec(200).during(1.minutes)).protocols(httpProtocol))
}
