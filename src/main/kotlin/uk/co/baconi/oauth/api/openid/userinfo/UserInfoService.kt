package uk.co.baconi.oauth.api.openid.userinfo

import uk.co.baconi.oauth.api.openid.Claims
import uk.co.baconi.oauth.api.openid.ScopesConfiguration
import uk.co.baconi.oauth.api.openid.ScopesConfigurationRepository
import uk.co.baconi.oauth.api.tokens.AccessToken
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

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

    // TODO - Replace with a data class, might help with custom serialisers?
    private fun getClaim(accessToken: AccessToken, claim: Claims): Pair<Claims, Any?> = claim to when (claim) {
        Claims.Subject -> accessToken.username
        Claims.Name -> TODO()
        Claims.GivenName -> TODO()
        Claims.FamilyName -> TODO()
        Claims.MiddleName -> TODO()
        Claims.Nickname -> TODO()
        Claims.PreferredUserName -> TODO()
        Claims.ProfileUrl -> TODO()
        Claims.PictureUrl -> "/assets/profile-images/${profileImages.random()}"
        Claims.Email -> TODO()
        Claims.EmailVerified -> TODO()
        Claims.Birthdate -> TODO()
        Claims.ZoneInfo -> TODO()
        Claims.Locale -> TODO()
        Claims.PhoneNumber -> TODO()
        Claims.PhoneNumberVerified -> TODO()
        Claims.Address -> TODO()
        Claims.UpdatedAt -> TODO()
    }

    private val profileImages: List<String> by lazy {
        getResourceFiles("/assets/profile-images/")
    }

    @Throws(IOException::class)
    fun getResourceFiles(path: String): List<String> = this::class.java.getResourceAsStream(path).use { stream ->
        return if (stream == null) {
            emptyList()
        } else {
            BufferedReader(InputStreamReader(stream)).readLines()
        }
    }
}