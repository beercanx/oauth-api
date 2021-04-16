package com.sbgcore.oauth.api.openid.userinfo

import com.sbgcore.oauth.api.ktor.auth.AccessTokenWithOpenId
import com.sbgcore.oauth.api.ktor.auth.authenticate
import com.sbgcore.oauth.api.tokens.AccessToken
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Route.userInfoRoute(
    userInfoService: UserInfoService
) {
    authenticate(AccessTokenWithOpenId) {
        get {
            when (val accessToken = call.principal<AccessToken>()) {
                is AccessToken -> call.respond(userInfoService.getUserInfo(accessToken))
                else -> call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}
