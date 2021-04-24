package com.sbgcore.oauth.api.openid.userinfo

import com.sbgcore.oauth.api.openid.Claims
import com.sbgcore.oauth.api.openid.ScopesConfiguration
import com.sbgcore.oauth.api.openid.ScopesConfigurationRepository
import com.sbgcore.oauth.api.tokens.AccessToken
import com.sbgcore.oauth.api.tokens.AccessTokenRepository
import java.time.OffsetDateTime.now
import java.util.*

class UserInfoService(private val scopesConfigurationRepository: ScopesConfigurationRepository) {

    // https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims
    fun getUserInfo(accessToken: AccessToken): UserInfoResponse {
        // TODO - Improve this is crude assuming individual sources of data, also only supports username.
        return accessToken
            .scopes
            .asSequence()
            .mapNotNull(scopesConfigurationRepository::findById)
            .flatMap(ScopesConfiguration::claims)
            .map { claim -> getClaim(accessToken, claim) }
            .toMap()
    }

    private fun getClaim(accessToken: AccessToken, claim: Claims): Pair<Claims, Any?> = claim to when(claim) {
        Claims.Subject -> accessToken.username
        Claims.Name -> TODO()
        Claims.GivenName -> TODO()
        Claims.FamilyName -> TODO()
        Claims.MiddleName -> TODO()
        Claims.Nickname -> TODO()
        Claims.PreferredUserName -> TODO()
        Claims.ProfileUrl -> TODO()
        Claims.PictureUrl -> TODO()
        Claims.WebsiteUrl -> TODO()
        Claims.Email -> TODO()
        Claims.EmailVerified -> TODO()
        Claims.Gender -> TODO()
        Claims.Birthdate -> TODO()
        Claims.ZoneInfo -> TODO()
        Claims.Locale -> TODO()
        Claims.PhoneNumber -> TODO()
        Claims.PhoneNumberVerified -> TODO()
        Claims.Address -> TODO()
        Claims.UpdatedAt -> TODO()
    }
}