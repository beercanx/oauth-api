package uk.co.baconi.oauth.api.user.info

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.claim.Claim
import uk.co.baconi.oauth.api.common.scope.ScopeConfiguration
import uk.co.baconi.oauth.api.common.scope.ScopeConfigurationRepository
import uk.co.baconi.oauth.api.common.token.AccessToken
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

class UserInfoService(private val scopeConfigurationRepository: ScopeConfigurationRepository) {

    // https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims
    fun getUserInfo(accessToken: AccessToken): UserInfo {
        return accessToken
            .scopes
            .asSequence()
            .mapNotNull(scopeConfigurationRepository::findById)
            .flatMap(ScopeConfiguration::claims)
            .fold(UserInfo.Builder()) { builder, claim -> getClaim(builder, accessToken, claim) }
            .build()
    }

    private fun getClaim(builder: UserInfo.Builder, accessToken: AccessToken, claim: Claim): UserInfo.Builder = when (claim) {
        Claim.Subject -> builder.apply { subject = accessToken.username }
        Claim.UpdatedAt -> builder.apply { updatedAt = LocalDateTime.now().toEpochSecond(UTC) }
    }

    @Serializable
    data class UserInfo(
        @SerialName("sub") val subject: AuthenticatedUsername?,
        @SerialName("updated_at") val updatedAt: Long?,
    ) {
        data class Builder(
            var subject: AuthenticatedUsername? = null,
            var updatedAt: Long? = null,
        ) {
            fun build(): UserInfo = UserInfo(
                subject = subject,
                updatedAt = updatedAt,
            )
        }
    }
}