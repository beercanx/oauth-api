package com.sbgcore.oauth.api.openid

import com.sbgcore.oauth.api.client.ClientConfigurationRepository
import com.sbgcore.oauth.api.openid.exchange.*
import com.sbgcore.oauth.api.openid.exchange.flows.assertion.AssertionRedemptionFlow
import com.sbgcore.oauth.api.openid.exchange.flows.authorization.AuthorizationCodeFlow
import com.sbgcore.oauth.api.openid.exchange.flows.password.PasswordFlow
import com.sbgcore.oauth.api.openid.exchange.flows.refresh.RefreshFlow
import com.sbgcore.oauth.api.openid.introspection.*
import io.ktor.routing.*

// TODO - Consider changing Application from the base library to a custom class so we don't need to include dependencies in method signature
fun Route.openIdRoutes(
    passwordFlow: PasswordFlow,
    refreshFlow: RefreshFlow,
    authorizationCodeFlow: AuthorizationCodeFlow,
    assertionRedemptionFlow: AssertionRedemptionFlow,
    introspectionService: IntrospectionService,
    clientConfigurationRepository: ClientConfigurationRepository
) {
    // TODO - Ensure cache control headers are set to prevent caching
    route("/openid/v1") {
        route("/authorize") {
            // TODO - Implement
        }
        route("/token") {
            exchangeRoute(
                passwordFlow,
                refreshFlow,
                authorizationCodeFlow,
                assertionRedemptionFlow,
                clientConfigurationRepository
            )
        }
        route("/introspect") {
            introspectionRoute(introspectionService)
        }
        route("/revoke") {
            // TODO - Implement
        }
    }
}
