package uk.co.baconi.oauth.api.session.info

import io.ktor.server.routing.*
import uk.co.baconi.oauth.api.common.CommonModule.common
import uk.co.baconi.oauth.api.common.DatabaseModule
import uk.co.baconi.oauth.api.common.TestAccessTokenModule
import uk.co.baconi.oauth.api.common.TestRefreshTokenModule
import uk.co.baconi.oauth.api.common.embeddedCommonServer
import uk.co.baconi.oauth.api.common.token.AccessTokenRepository
import uk.co.baconi.oauth.api.common.token.AccessTokenService
import uk.co.baconi.oauth.api.common.token.RefreshTokenRepository
import uk.co.baconi.oauth.api.common.token.RefreshTokenService

object SessionInfoServer : DatabaseModule, SessionInfoRoute, TestAccessTokenModule, TestRefreshTokenModule {

    private val accessTokenRepository = AccessTokenRepository(accessTokenDatabase)
    override val accessTokenService = AccessTokenService(accessTokenRepository)

    private val refreshTokenRepository = RefreshTokenRepository(refreshTokenDatabase)
    override val refreshTokenService = RefreshTokenService(refreshTokenRepository)

    override val sessionInfoService = SessionInfoService(accessTokenRepository, refreshTokenRepository)

    fun start() {
        embeddedCommonServer {
            common()
            routing {
                sessionInfo()
            }
            generateTestAccessTokens()
            generateTestRefreshTokens()
        }.start(true)
    }
}