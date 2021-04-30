package com.sbgcore.oauth.api.openid.userinfo

import com.sbgcore.oauth.api.ktor.auth.authorizeAccessToken
import com.sbgcore.oauth.api.ktor.auth.authenticate
import com.sbgcore.oauth.api.openid.Scopes.OpenId
import com.sbgcore.oauth.api.tokens.AccessToken
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.userInfoRoute(
    userInfoService: UserInfoService
) {
    authenticate(AccessToken::class) {
        get {
            authorizeAccessToken(OpenId) { accessToken ->
                call.respond(userInfoService.getUserInfo(accessToken))
            }
        }
    }
}
