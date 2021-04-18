package com.sbgcore.oauth.api.ktor.auth

import com.sbgcore.oauth.api.ktor.ApplicationContext
import com.sbgcore.oauth.api.ktor.auth.bearer.oAuth2BearerAuthChallenge
import com.sbgcore.oauth.api.openid.Scopes
import com.sbgcore.oauth.api.tokens.AccessToken
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.http.HttpHeaders.WWWAuthenticate

const val AccessToken = "AccessToken"

typealias AccessTokenBlock = suspend ApplicationContext.(AccessToken) -> Unit

/**
 * Requires an [AccessToken] principle to have been issued the [scopes] before the inner block is called:
 *  - If it does not have the [scopes] issued then a 403 with a [WWWAuthenticate] header will be issued.
 *  - If there is no [AccessToken] then an [IllegalStateException] will be thrown, which will generate a 500 response.
 */
suspend fun ApplicationContext.requireScopes(vararg scopes: Scopes, block: AccessTokenBlock) {
    return requireScopes(scopes.toSet(), block)
}

/**
 * Requires an [AccessToken] principle to have been issued the [scopes] before the inner block is called:
 *  - If it does not have the [scopes] issued then a 403 with a [WWWAuthenticate] header will be issued.
 *  - If there is no [AccessToken] then an [IllegalStateException] will be thrown, which will generate a 500 response.
 */
suspend fun ApplicationContext.requireScopes(scopes: Set<Scopes>, block: AccessTokenBlock) {

    // If the application is setup correctly this should not be null.
    val accessToken = checkNotNull(call.principal<AccessToken>()) {
        "Access Token should not be null"
    }

    when {

        // Check that the access token contains all the required scopes.
        accessToken.scopes.containsAll(scopes) -> block(accessToken)

        // This access token is not authorised to call the application block.
        else -> call.respond(ForbiddenResponse(oAuth2BearerAuthChallenge("skybettingandgaming", scopes)))
    }
}