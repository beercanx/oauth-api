package com.sbgcore.oauth.api.scenarios

import com.sbgcore.oauth.api.endpoints.Introspection.Operations.introspectAccessToken
import com.sbgcore.oauth.api.endpoints.TokenExchange.Operations.passwordFlow
import com.sbgcore.oauth.api.endpoints.UserInfo.Operations.userInfoWithAccessToken
import com.sbgcore.oauth.api.feeders.Clients.Setup.withClient
import com.sbgcore.oauth.api.feeders.Customers.Feeders.customers
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class LoginIntrospectUserInfoSimulation extends Simulation {

  private val scn = scenario("Login, Introspect and get User Info")
    .exec(withClient("consumer-z", "7XLlyzjRpvICEkNrsgtOuuj1S30Bj9Xu")) // TODO - Extract into config?
    .feed(customers.random)
    .exec(passwordFlow)
    .exec(introspectAccessToken)
    .exec(userInfoWithAccessToken)

  private val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  setUp(scn.inject(atOnceUsers(1)).protocols(httpProtocol))
}
