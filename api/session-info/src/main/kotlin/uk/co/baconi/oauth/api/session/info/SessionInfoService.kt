package uk.co.baconi.oauth.api.session.info

import uk.co.baconi.oauth.api.common.authentication.AuthenticatedSession
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeRepository
import uk.co.baconi.oauth.api.common.token.AccessTokenRepository
import uk.co.baconi.oauth.api.common.token.RefreshTokenRepository
import uk.co.baconi.oauth.api.session.info.SessionInfoResponse.*

class SessionInfoService(
    private val accessTokenRepository: AccessTokenRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val authorisationCodeRepository: AuthorisationCodeRepository,
) {
    fun getSessionInfo(authenticated: AuthenticatedSession?): SessionInfoResponse = when(authenticated) {
        null -> SessionInfoResponse(null, null)
        else -> SessionInfoResponse(
            session = authenticated,
            tokens = Tokens(
                authorisations = authorisationCodeRepository.findAllByUsername(authenticated.username).map(::Token),
                accessTokens = accessTokenRepository.findAllByUsername(authenticated.username).map(::Token),
                refreshTokens = refreshTokenRepository.findAllByUsername(authenticated.username).map(::Token)
            )
        )
    }
}