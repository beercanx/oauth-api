package uk.co.baconi.oauth.api.common

import io.ktor.http.auth.HttpAuthHeader.*
import io.ktor.http.auth.HttpAuthHeader.Parameters.Realm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import uk.co.baconi.oauth.api.common.client.ClientSecretService
import uk.co.baconi.oauth.api.common.client.ConfidentialClient
import uk.co.baconi.oauth.api.common.client.PublicClient
import uk.co.baconi.oauth.api.common.ktor.auth.basic
import uk.co.baconi.oauth.api.common.ktor.auth.bearer
import uk.co.baconi.oauth.api.common.ktor.auth.form
import uk.co.baconi.oauth.api.common.token.AccessToken
import uk.co.baconi.oauth.api.common.token.AccessTokenService

interface AuthenticationModule {

    val clientSecretService: ClientSecretService
    val accessTokenService: AccessTokenService

    fun Application.authentication() {

        log.info("Registering the AuthenticationModule.authentication() module")

        val realm = "oauth-api" // TODO - Move into config

        install(Authentication) {
            basic<ConfidentialClient> {
                this.realm = realm
                validate { (clientId, clientSecret) ->
                    clientSecretService.authenticate(clientId, clientSecret)
                }
            }
            form<PublicClient> {
                userParamName = "client_id"
                passwordParamName = "client_id"
                challenge {
                    call.respond(UnauthorizedResponse(Parameterized("Body", mapOf(Realm to realm))))
                }
                validate { (clientId, _) ->
                    clientSecretService.authenticate(clientId)
                }
            }
            bearer<AccessToken> {
                this.realm = realm
                authenticate { (token) ->
                    accessTokenService.authenticate(token)
                }
            }
        }
    }
}