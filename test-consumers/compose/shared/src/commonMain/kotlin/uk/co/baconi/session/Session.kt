package uk.co.baconi.session

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uk.co.baconi.session.oauth.AccessToken
import uk.co.baconi.session.oauth.RefreshToken
import uk.co.baconi.session.oauth.State

@Serializable
data class Session(
    @SerialName("access_token") val accessToken: AccessToken,
    @SerialName("refresh_token") val refreshToken: RefreshToken,
    @SerialName("expires_in") val expires: Long,
    @Serializable(with = SpaceDelimitedSerializer::class) @SerialName("scope") val scopes: Set<String>,
    @SerialName("token_type") val tokenType: String,
    val state: State? = null,
)
