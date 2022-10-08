package uk.co.baconi.oauth.api.userinfo

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.resources.*
import io.ktor.server.routing.Route
import uk.co.baconi.oauth.api.ktor.auth.authenticate
import uk.co.baconi.oauth.api.ktor.auth.authoriseAccessToken
import uk.co.baconi.oauth.api.ktor.resourcesFormat
import uk.co.baconi.oauth.api.scopes.Scopes.OpenId
import uk.co.baconi.oauth.api.tokens.AccessToken

interface UserInfoRoute {

    val userInfoService: UserInfoService

    fun Route.userInfo() {
        authenticate(AccessToken::class) {
            get<UserInfoLocation> {
                authoriseAccessToken(OpenId) { accessToken ->
                    call.respond(userInfoService.getUserInfo(accessToken, application.resourcesFormat))
                }
            }
        }
    }
}

