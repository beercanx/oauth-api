package uk.co.baconi.oauth.api.common

// TODO - Decide if we want to fix this problem with code or by choosing a database technology that supports it.
//import io.ktor.server.application.*
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeRepository
//import uk.co.baconi.oauth.api.common.token.AccessTokenRepository
//import uk.co.baconi.oauth.api.common.token.RefreshTokenRepository
//
//interface DatabaseExpirationModule {
//
//    val accessTokenRepository: AccessTokenRepository
//    val refreshTokenRepository: RefreshTokenRepository
//    val authorisationCodeRepository: AuthorisationCodeRepository
//
//    fun Application.databaseExpiration() {
//
//        log.info("Registering the DatabaseModule.databaseExpiration() module")
//
//        // Every second, look for and remove any expired tokens or codes - TODO - Extract delay into config.
//        val accessTokenJob = doIndefinitely(1_000, accessTokenRepository::deleteExpired)
//        val refreshTokenJob = doIndefinitely(1_000, refreshTokenRepository::deleteExpired)
//        val authorisationCodeJob = doIndefinitely(1_000, authorisationCodeRepository::deleteExpired)
//    }
//
//    private fun Application.doIndefinitely(timeMillis: Long, block: suspend () -> Unit): Job = launch {
//        while (true) {
//            block()
//            delay(timeMillis)
//        }
//    }
//}