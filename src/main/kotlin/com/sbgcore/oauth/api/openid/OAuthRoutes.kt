package com.sbgcore.oauth.api.openid

import com.sbgcore.oauth.api.openid.exchange.ExchangeRoute
import com.sbgcore.oauth.api.openid.introspection.IntrospectionRoute
import com.sbgcore.oauth.api.openid.userinfo.UserInfoRoute
import io.ktor.routing.*

interface OAuthRoutes : ExchangeRoute, IntrospectionRoute, UserInfoRoute {

    fun Route.oAuthRoutes() {
        route("/oauth/v1") {
            route("/authorize") {
                // TODO - Implement
            }
            route("/token") {
                exchangeRoute()
            }
            route("/introspect") {
                introspectionRoute()
            }
            route("/revoke") {
                // TODO - Implement
            }
            route("/userinfo") {
                userInfoRoute()
            }
        }
    }
}
