package uk.co.baconi.oauth.api.user.info

import uk.co.baconi.oauth.api.common.claim.Claim
import uk.co.baconi.oauth.api.common.scope.ScopeConfiguration
import uk.co.baconi.oauth.api.common.scope.ScopeConfigurationRepository
import uk.co.baconi.oauth.api.common.token.AccessToken
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

class UserInfoService(private val scopeConfigurationRepository: ScopeConfigurationRepository) {

    // https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims
    fun getUserInfo(accessToken: AccessToken): Map<Claim, Any?> {
        return accessToken
            .scopes
            .asSequence()
            .mapNotNull(scopeConfigurationRepository::findById)
            .flatMap(ScopeConfiguration::claims)
            .map { claim -> getClaim(accessToken, claim) }
            .toMap()
    }

    private fun getClaim(accessToken: AccessToken, claim: Claim): Pair<Claim, Any?> = claim to when (claim) {
        Claim.Subject -> accessToken.username
        Claim.UpdatedAt -> LocalDateTime.now().toEpochSecond(UTC)
    }
}