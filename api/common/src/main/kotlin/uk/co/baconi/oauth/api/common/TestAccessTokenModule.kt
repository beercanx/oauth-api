package uk.co.baconi.oauth.api.common

import io.ktor.server.application.*
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.scope.Scope
import uk.co.baconi.oauth.api.common.token.AccessTokenService

@Deprecated("This is intended to be removed once code complete")
interface TestAccessTokenModule {

    val accessTokenService: AccessTokenService

    @Deprecated("This is intended to be removed once code complete")
    fun Application.generateTestAccessTokens() {

        log.info("Registering the TestAccessTokenModule.generateTestAccessTokens() module")

        val accessToken = accessTokenService.issue(
            AuthenticatedUsername("aardvark"),
            ClientId("consumer-x"),
            setOf(Scope.Basic)
        )

        log.info("Generated Access Token: ${accessToken.value}")
    }
}