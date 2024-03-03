package uk.co.baconi.oauth.api.common

import io.ktor.server.application.*
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.scope.Scope
import uk.co.baconi.oauth.api.common.token.AccessTokenService
import uk.co.baconi.oauth.api.common.token.RefreshTokenService

@Deprecated("This is intended to be removed once code complete")
interface TestRefreshTokenModule {

    val refreshTokenService: RefreshTokenService

    @Deprecated("This is intended to be removed once code complete")
    fun Application.generateTestRefreshTokens() {

        log.info("Registering the TestRefreshTokenModule.generateTestAccessTokens() module")

        val refreshToken = refreshTokenService.issue(
            AuthenticatedUsername("aardvark"),
            ClientId("consumer-x"),
            setOf(Scope("basic"))
        )

        log.info("Generated Access Token: ${refreshToken.value}")
    }
}