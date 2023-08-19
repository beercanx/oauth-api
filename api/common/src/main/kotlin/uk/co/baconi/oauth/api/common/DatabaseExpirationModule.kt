package uk.co.baconi.oauth.api.common

import io.ktor.server.application.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeRepository
import uk.co.baconi.oauth.api.common.token.AccessTokenRepository

interface DatabaseExpirationModule {

    val accessTokenRepository: AccessTokenRepository
    val authorisationCodeRepository: AuthorisationCodeRepository

    fun Application.databaseExpiration() {

        log.info("Registering the DatabaseModule.databaseExpiration() module")

        // Every second, look for and remove any expired tokens or codes - TODO - Extract delay into config.
        doIndefinitely(1_000, accessTokenRepository::deleteExpired)
        doIndefinitely(1_000, authorisationCodeRepository::deleteExpired)
    }

    private fun Application.doIndefinitely(timeMillis: Long, block: suspend () -> Unit) {
        launch {
            while (true) {
                block()
                delay(timeMillis)
            }
        }
    }
}