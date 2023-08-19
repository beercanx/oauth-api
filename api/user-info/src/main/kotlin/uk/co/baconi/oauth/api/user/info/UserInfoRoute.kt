package uk.co.baconi.oauth.api.user.info

import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.http.auth.*
import io.ktor.http.auth.AuthScheme.Bearer
import io.ktor.http.auth.HttpAuthHeader.Companion.bearerAuthChallenge
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.baconi.oauth.api.common.ktor.auth.authenticate
import uk.co.baconi.oauth.api.common.scope.Scope.OpenId
import uk.co.baconi.oauth.api.common.token.AccessToken

interface UserInfoRoute {

    val userInfoService: UserInfoService

    fun Route.userInfo() {

        application.log.info("Registering the UserInfoRoute.userInfo() routes")

        val realm = "oauth-api" // TODO - Move into config

        route("/userinfo") {
            authenticate(AccessToken::class) {
                get {

                    val accessToken = call.principal<AccessToken>()

                    when {

                        // Should not be null...
                        accessToken == null -> call.respond(InternalServerError)

                        // This access token is not authorised to call the application block.
                        !accessToken.scopes.contains(OpenId) -> {
                            call.respond(
                                ForbiddenResponse(
                                    bearerAuthChallenge(Bearer, realm)
                                )
                            )
                        }

                        // Check that the access token contains all the required scopes.
                        else -> call.respond(userInfoService.getUserInfo(accessToken))
                    }
                }
            }
        }
    }
}

