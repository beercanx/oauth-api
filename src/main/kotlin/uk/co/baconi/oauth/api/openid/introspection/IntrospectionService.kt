package uk.co.baconi.oauth.api.openid.introspection

import uk.co.baconi.oauth.api.tokens.AccessToken
import uk.co.baconi.oauth.api.tokens.AccessTokenRepository
import java.time.OffsetDateTime.now
import java.util.*

class IntrospectionService(private val accessTokenRepository: AccessTokenRepository) {

    fun introspect(request: IntrospectionRequest) = lookup(request.token).toIntrospectionResponse()

    fun introspect(request: IntrospectionRequestWithHint) = lookup(request.token).toIntrospectionResponse()

    private fun lookup(token: String): AccessToken? = try {
        accessTokenRepository.findByValue(token)
    } catch (exception: Exception) {
        null
    }

    private fun AccessToken?.toIntrospectionResponse(): IntrospectionResponse {
        return when {
            this == null -> InactiveIntrospectionResponse()
            hasExpired() -> InactiveIntrospectionResponse()
            else -> ActiveIntrospectionResponse(
                scope = scopes,
                clientId = clientId,
                username = username,
                subject = username,
                expirationTime = expiresAt.toEpochSecond(),
                issuedAt = issuedAt.toEpochSecond(),
                notBefore = notBefore.toEpochSecond()
            )
        }
    }
}