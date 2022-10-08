package uk.co.baconi.oauth.api.authorisation

import uk.co.baconi.oauth.api.authentication.Authentication
import uk.co.baconi.oauth.api.exchange.PasswordRequest
import uk.co.baconi.oauth.api.tokens.AccessToken
import uk.co.baconi.oauth.api.tokens.AccessTokenService

class AuthorisationService(
    private val authorisationCodeRepository: AuthorisationCodeRepository,
    private val accessTokenService: AccessTokenService
) {

    fun authorise(authorisationCode: AuthorisationCode): AccessToken {

        // TODO - Check that the scopes can be issued to the user

        return accessTokenService.issue(
            username = authorisationCode.username,
            clientId = authorisationCode.clientId,
            scopes = authorisationCode.requestedScope // TODO - Reduce or fail based on checks
        )
    }

    fun authorise(request: PasswordRequest, authenticated: Authentication.Success): AccessToken {

        // TODO - Check that the scopes can be issued to the user

        return accessTokenService.issue(
            username = authenticated.username,
            clientId = request.principal.id,
            scopes = request.scopes // TODO - Reduce or fail based on checks
        )
    }
}
