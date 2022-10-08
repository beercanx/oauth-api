package uk.co.baconi.oauth.api.openid.userinfo

import uk.co.baconi.oauth.api.ktor.auth.authenticate
import uk.co.baconi.oauth.api.ktor.auth.authorizeAccessToken
import uk.co.baconi.oauth.api.openid.Scopes.OpenId
import uk.co.baconi.oauth.api.tokens.AccessToken
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

interface UserInfoRoute {

    val userInfoService: UserInfoService

    fun Route.userInfoRoute() {
        authenticate(AccessToken::class) {
            get {
                authorizeAccessToken(OpenId) { accessToken ->
                    call.respond(userInfoService.getUserInfo(accessToken, application.locations))
                }
            }
        }
    }
}

