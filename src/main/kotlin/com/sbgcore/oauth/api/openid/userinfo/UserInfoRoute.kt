package com.sbgcore.oauth.api.openid.userinfo

import com.sbgcore.oauth.api.ktor.auth.AccessToken
import com.sbgcore.oauth.api.ktor.auth.requireAccessTokenWithScopes
import com.sbgcore.oauth.api.openid.Scopes.OpenId
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.userInfoRoute(
    userInfoService: UserInfoService
) {
    authenticate(AccessToken) {
        get {
            requireAccessTokenWithScopes(OpenId) { accessToken ->
                call.respond(userInfoService.getUserInfo(accessToken))
            }
        }
    }
}
