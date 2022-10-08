package uk.co.baconi.oauth.api.common

import io.ktor.http.auth.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import uk.co.baconi.oauth.api.common.client.ClientSecretService
import uk.co.baconi.oauth.api.common.client.ConfidentialClient
import uk.co.baconi.oauth.api.common.client.PublicClient
import uk.co.baconi.oauth.api.common.ktor.auth.basic
import uk.co.baconi.oauth.api.common.ktor.auth.form
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
                    UnauthorizedResponse(
                        HttpAuthHeader.Parameterized(
                            "Body",
                            mapOf(HttpAuthHeader.Parameters.Realm to realm)
                        )
                    )
                }
                validate { (clientId, _) ->
                    clientSecretService.authenticate(clientId)
                }
            }
            // TODO - Bearer for User Info endpoint
//            bearer<AccessToken> {
//                realm = REALM
//                validate { (token) ->
//                    accessTokenService.authenticate(token)
//                }
//            }
        }
    }
}