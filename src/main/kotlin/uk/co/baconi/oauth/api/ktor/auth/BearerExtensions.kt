package uk.co.baconi.oauth.api.ktor.auth

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.HttpHeaders.WWWAuthenticate
import io.ktor.response.*
import uk.co.baconi.oauth.api.OAuth2Server.REALM
import uk.co.baconi.oauth.api.ktor.ApplicationContext
import uk.co.baconi.oauth.api.ktor.auth.bearer.BearerAuthenticationProvider
import uk.co.baconi.oauth.api.ktor.auth.bearer.bearer
import uk.co.baconi.oauth.api.ktor.auth.bearer.bearerAuthChallenge
import uk.co.baconi.oauth.api.scopes.Scopes
import uk.co.baconi.oauth.api.tokens.AccessToken
import kotlin.reflect.jvm.jvmName

/**
 * Provides a typed way to define which principle type this OAuth2 bearer auth will provide.
 */
inline fun <reified T : Principal> Authentication.Configuration.bearer(
    noinline configure: BearerAuthenticationProvider.Configuration.() -> Unit,
) {
    bearer(T::class.jvmName, configure)
}

typealias AccessTokenBlock = suspend ApplicationContext.(AccessToken) -> Unit

/**
 * Attempts to authorise an [AccessToken] principle against the required [Scopes] before the inner block is called:
 *  - If it does not have the required [Scopes] issued then a 403 with a [WWWAuthenticate] header will be issued.
 *  - If there is no [AccessToken] then an [IllegalStateException] will be thrown, which will generate a 500 response.
 */
suspend fun ApplicationContext.authoriseAccessToken(vararg required: Scopes, block: AccessTokenBlock) {
    return authoriseAccessToken(required.toSet(), block)
}

/**
 * Attempts to authorise an [AccessToken] principle against the required [Scopes] before the inner block is called:
 *  - If it does not have the required [Scopes] issued then a 403 with a [WWWAuthenticate] header will be issued.
 *  - If there is no [AccessToken] then an [IllegalStateException] will be thrown, which will generate a 500 response.
 */
suspend fun ApplicationContext.authoriseAccessToken(required: Set<Scopes>, block: AccessTokenBlock) {

    // If the application is setup correctly this should not be null.
    val accessToken = checkNotNull(call.principal<AccessToken>()) {
        "Access Token should not be null"
    }

    when {

        // Check that the access token contains all the required scopes.
        accessToken.scopes.containsAll(required) -> block(accessToken)

        // This access token is not authorised to call the application block.
        else -> call.respond(ForbiddenResponse(bearerAuthChallenge(REALM, required)))
    }
}