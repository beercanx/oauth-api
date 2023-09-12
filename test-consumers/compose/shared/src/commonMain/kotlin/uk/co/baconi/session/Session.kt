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

sealed interface Session {
    val accessToken: AccessToken
    val refreshToken: RefreshToken
    val expires: Long // TODO - Sort out multiplatform date time object.
    val scopes: Set<String>
}

@Serializable
data class Success(
    @SerialName("access_token") override val accessToken: AccessToken,
    @SerialName("refresh_token") override val refreshToken: RefreshToken,
    @SerialName("expires_in") override val expires: Long,
    @Serializable(with = SpaceDelimitedSerializer::class) @SerialName("scope") override val scopes: Set<String>,
    @SerialName("token_type") val tokenType: String,
    val state: String? = null,
) : Session
