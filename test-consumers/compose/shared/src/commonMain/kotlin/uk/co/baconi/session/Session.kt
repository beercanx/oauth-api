package uk.co.baconi.session

//import kotlinx.datetime.Instant
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
    //val expires: Instant
    val expires: Long
    val scopes: Set<String>
}

sealed interface TokenResponse

@Serializable
data class Success(
    @SerialName("access_token") override val accessToken: AccessToken,
    @SerialName("refresh_token") override val refreshToken: RefreshToken,
    //@Serializable(with = ExpiresInSerializer::class) @SerialName("expires_in") override val expires: Instant,
    @SerialName("expires_in") override val expires: Long,
    @Serializable(with = SpaceDelimitedSerializer::class) @SerialName("scope") override val scopes: Set<String>,
    @SerialName("token_type") val tokenType: String,
    val state: String?,
) : Session, TokenResponse

@Serializable
data class Failed(
    val error: ErrorType,
    @SerialName("error_description") val errorDescription: String,
) : TokenResponse

@Serializable
enum class ErrorType {
    @SerialName("invalid_request") InvalidRequest,
    @SerialName("invalid_client") InvalidClient,
    @SerialName("invalid_grant") InvalidGrant,
    @SerialName("unauthorized_client") UnauthorizedClient,
    @SerialName("unsupported_grant_type") UnsupportedGrantType,
    @SerialName("invalid_scope") InvalidScope,
}

val sessionSerializersModule = SerializersModule {
    polymorphic(TokenResponse::class) {
        subclass(Success::class)
        subclass(Failed::class)
    }
}
