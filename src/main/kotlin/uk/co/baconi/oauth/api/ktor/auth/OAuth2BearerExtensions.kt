package uk.co.baconi.oauth.api.ktor.auth

import uk.co.baconi.oauth.api.OAuth2Server.REALM
import uk.co.baconi.oauth.api.ktor.ApplicationContext
import uk.co.baconi.oauth.api.ktor.auth.bearer.OAuth2BearerAuthenticationProvider
import uk.co.baconi.oauth.api.ktor.auth.bearer.oAuth2Bearer
import uk.co.baconi.oauth.api.ktor.auth.bearer.oAuth2BearerAuthChallenge
import uk.co.baconi.oauth.api.scopes.Scopes
import uk.co.baconi.oauth.api.tokens.AccessToken
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.HttpHeaders.WWWAuthenticate
import io.ktor.response.*
import kotlin.reflect.jvm.jvmName

/**
 * Provides a typed way to define which principle type this OAuth2 bearer auth will provide.
 */
inline fun <reified T : Principal> Authentication.Configuration.oAuth2Bearer(
    noinline configure: OAuth2BearerAuthenticationProvider.Configuration.() -> Unit,
) {
    oAuth2Bearer(T::class.jvmName, configure)
}

typealias AccessTokenBlock = suspend ApplicationContext.(AccessToken) -> Unit

/**
 * Attempts to authorize an [AccessToken] principle against the required [Scopes] before the inner block is called:
 *  - If it does not have the required [Scopes] issued then a 403 with a [WWWAuthenticate] header will be issued.
 *  - If there is no [AccessToken] then an [IllegalStateException] will be thrown, which will generate a 500 response.
 */
suspend fun ApplicationContext.authorizeAccessToken(vararg required: Scopes, block: AccessTokenBlock) {
    return authorizeAccessToken(required.toSet(), block)
}

/**
 * Attempts to authorize an [AccessToken] principle against the required [Scopes] before the inner block is called:
 *  - If it does not have the required [Scopes] issued then a 403 with a [WWWAuthenticate] header will be issued.
 *  - If there is no [AccessToken] then an [IllegalStateException] will be thrown, which will generate a 500 response.
 */
suspend fun ApplicationContext.authorizeAccessToken(required: Set<Scopes>, block: AccessTokenBlock) {

    // If the application is setup correctly this should not be null.
    val accessToken = checkNotNull(call.principal<AccessToken>()) {
        "Access Token should not be null"
    }

    when {

        // Check that the access token contains all the required scopes.
        accessToken.scopes.containsAll(required) -> block(accessToken)

        // This access token is not authorised to call the application block.
        else -> call.respond(ForbiddenResponse(oAuth2BearerAuthChallenge(REALM, required)))
    }
}