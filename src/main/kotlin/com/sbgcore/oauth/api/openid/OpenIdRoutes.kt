package com.sbgcore.oauth.api.openid

import com.sbgcore.oauth.api.ktor.params
import com.sbgcore.oauth.api.openid.exchange.PasswordFlow
import com.sbgcore.oauth.api.openid.exchange.RefreshFlow
import com.sbgcore.oauth.api.openid.exchange.ScopeParameter
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
import java.lang.RuntimeException

@KtorExperimentalAPI
fun Application.openIdRoutes(passwordFlow: PasswordFlow, refreshFlow: RefreshFlow) {
    routing {
        route("/openid/v1/token") {

            // Required parameter and value for password flow
            param("grant_type", "password") {

                // Required parameters for password flow
                params("scope", "username", "password") {

                    // This flow requires a valid client id and secret
                    authenticate("client_id_and_secret") {

                        get {
                            val client = call.principal<AuthenticatedClient>() ?: throw RuntimeException("Err")
                            val scopes = call.parameters.getOrFail<ScopeParameter>("scope").scopes
                            val username = call.parameters.getOrFail("username")
                            val password = call.parameters.getOrFail("password")

                            call.respond(passwordFlow.exchange(client, scopes, username, password))
                        }
                    }
                }
            }

            // Required parameter and value for refresh flow
            param("grant_type", "refresh") {

                // Required parameters for password flow
                params("refresh_token", "scope") {

                    // This flow requires a valid client id and secret
                    authenticate("client_id_and_secret") {

                        get {
                            val client = call.principal<AuthenticatedClient>() ?: throw RuntimeException("Err")
                            val refreshToken = call.parameters.getOrFail("refresh_token")
                            val scopes = call.parameters.getOrFail<ScopeParameter>("scope").scopes

                            call.respond(refreshFlow.exchange(client, refreshToken, scopes))
                        }
                    }
                }
            }
        }
    }
}