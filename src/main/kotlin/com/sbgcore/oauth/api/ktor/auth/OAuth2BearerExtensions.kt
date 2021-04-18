package com.sbgcore.oauth.api.ktor.auth

import com.sbgcore.oauth.api.ktor.ApplicationContext
import com.sbgcore.oauth.api.openid.Scopes
import com.sbgcore.oauth.api.tokens.AccessToken
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.HttpStatusCode.Companion.InternalServerError
import io.ktor.response.*

const val AccessToken = "AccessToken"

typealias AccessTokenBlock = suspend ApplicationContext.(AccessToken) -> Unit

suspend fun ApplicationContext.requireAccessTokenWithScopes(vararg scopes: Scopes, block: AccessTokenBlock) {
    return requireAccessTokenWithScopes(scopes.toSet(), block)
}

suspend fun ApplicationContext.requireAccessTokenWithScopes(scopes: Set<Scopes>, block: AccessTokenBlock) {
    val accessToken = call.principal<AccessToken>()
    when {
        // If the application is setup correctly this should not be null.
        accessToken == null -> call.respond(InternalServerError)

        // Check that the access token contains all the required scopes.
        accessToken.scopes.containsAll(scopes) -> block(accessToken)

        // This access token is not authorised to call the application block.
        else -> TODO("Handle 403")
    }
}