package uk.co.baconi.oauth.api.token.introspection

import uk.co.baconi.oauth.api.common.token.AccessToken
import uk.co.baconi.oauth.api.common.token.AccessTokenRepository
import uk.co.baconi.oauth.api.common.token.TokenType

class IntrospectionService(private val accessTokenRepository: AccessTokenRepository) {

    fun introspect(request: IntrospectionRequest.Valid): IntrospectionResponse {
        return accessTokenRepository
            .findById(request.token)
            .toIntrospectionResponse()
    }

    private fun AccessToken?.toIntrospectionResponse(): IntrospectionResponse {
        return when {
            this == null -> IntrospectionResponse.Inactive()
            hasExpired() -> IntrospectionResponse.Inactive()
            isBefore() -> IntrospectionResponse.Inactive()
            // TODO - Decide out if we want to block any Confident client from introspecting any token.
            else -> IntrospectionResponse.Active(
                scope = scopes,
                clientId = clientId,
                username = username,
                tokenType = TokenType.Bearer,
                expirationTime = expiresAt.epochSecond,
                issuedAt = issuedAt.epochSecond,
                notBefore = notBefore.epochSecond,
                subject = username,
            )
        }
    }
}