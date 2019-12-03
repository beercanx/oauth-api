package com.sbgcore.oauth.api.openid

import com.sbgcore.oauth.api.ktor.params
import com.sbgcore.oauth.api.openid.exchange.PasswordFlow
import com.sbgcore.oauth.api.openid.exchange.RefreshFlow
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.param
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.getOrFail

@KtorExperimentalAPI
fun Application.openIdRoutes(passwordFlow: PasswordFlow, refreshFlow: RefreshFlow) {
    routing {
        route("/openid/v1/token") {

            // Required parameter of certain type
            param("grant_type", "password") {

                // Required parameters
                params("client_id", "client_secret", "scope", "username", "password") {

                    // This flow requires a valid client id and secret
                    authenticate("client_id_and_secret") {

                        get {
                            val scope = call.parameters.getOrFail("scope")
                            val username = call.parameters.getOrFail("username")
                            val password = call.parameters.getOrFail("password")

                            // TODO - Work out how to handle null better
                            val client = call.principal<AuthenticatedClient>()!!

                            call.respond(passwordFlow.exchange(client, scope, username, password))
                        }
                    }
                }
            }

            param("grant_type", "refresh") { // Required parameter of certain type

                get {
                    call.respond(refreshFlow.exchange())
                }
            }
        }
    }
}