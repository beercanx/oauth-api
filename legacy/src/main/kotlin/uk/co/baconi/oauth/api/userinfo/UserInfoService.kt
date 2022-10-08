package uk.co.baconi.oauth.api.userinfo

import io.ktor.resources.*
import io.ktor.resources.serialization.*
import uk.co.baconi.oauth.api.assets.StaticAssetsLocation.ProfileImagesLocation
import uk.co.baconi.oauth.api.ktor.href
import uk.co.baconi.oauth.api.scopes.ScopesConfiguration
import uk.co.baconi.oauth.api.scopes.ScopesConfigurationRepository
import uk.co.baconi.oauth.api.tokens.AccessToken
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class UserInfoService(
    private val scopesConfigurationRepository: ScopesConfigurationRepository,
) {

    // https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims
    fun getUserInfo(accessToken: AccessToken, resourcesFormat: ResourcesFormat): UserInfoResponse {
        // TODO - Improve this is crude assuming individual sources of data, also only supports username.
        return accessToken
            .scopes
            .asSequence()
            .mapNotNull(scopesConfigurationRepository::findById)
            .flatMap(ScopesConfiguration::claims)
            .map { claim -> getClaim(accessToken, claim, resourcesFormat) }
            .toMap()
    }

    // TODO - Replace with a data class, might help with custom serialisers?
    private fun getClaim(accessToken: AccessToken, claim: Claims, resourcesFormat: ResourcesFormat): Pair<Claims, Any?> =
        claim to when (claim) {
            Claims.Subject -> accessToken.username
            Claims.Name -> TODO()
            Claims.GivenName -> TODO()
            Claims.FamilyName -> TODO()
            Claims.MiddleName -> TODO()
            Claims.Nickname -> TODO()
            Claims.PreferredUserName -> TODO()
            Claims.ProfileUrl -> TODO()
            Claims.PictureUrl -> resourcesFormat.href(ProfileImagesLocation(profileImages.random()))
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