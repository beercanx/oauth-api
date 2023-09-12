package uk.co.baconi.session

import kotlin.jvm.JvmInline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@JvmInline
@Serializable
value class AccessToken(val value: String)

@JvmInline
@Serializable
value class RefreshToken(val value: String)

@Serializable
data class Session(
    @SerialName("access_token") val accessToken: AccessToken,
    @SerialName("refresh_token") val refreshToken: RefreshToken,
    @SerialName("expires_in") val expires: Long,
    @Serializable(with = SpaceDelimitedSerializer::class) @SerialName("scope") val scopes: Set<String>,
    @SerialName("token_type") val tokenType: String,
    val state: String? = null,
)
