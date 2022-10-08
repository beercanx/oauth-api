package uk.co.baconi.oauth.api.common

import de.mkammerer.argon2.Argon2Factory
import io.ktor.server.application.*
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.scope.Scope
import uk.co.baconi.oauth.api.common.token.AccessTokenService
import uk.co.baconi.oauth.common.authentication.*

@Deprecated("This is intended to be removed once code complete")
interface TestAccessTokenModule {

    val accessTokenService: AccessTokenService

    @Deprecated("This is intended to be removed once code complete")
    fun Application.generateTestAccessTokens() {

        log.info("Registering the TestAccessTokenModule.generateTestAccessTokens() module")

        val accessToken = accessTokenService.issue(
            AuthenticatedUsername("aardvark"),
            ClientId("consumer-x"),
            setOf(Scope.OpenId)
        )

        log.info("Generated Access Token: ${accessToken.value}")
    }
}