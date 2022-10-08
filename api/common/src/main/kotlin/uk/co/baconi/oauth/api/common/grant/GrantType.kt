package uk.co.baconi.oauth.api.common.grant

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class GrantType(val value: String) {
    @SerialName("authorization_code") AuthorisationCode("authorization_code"),
    @SerialName("password") Password("password");
    //@SerialName("refresh_token") RefreshToken("refresh_token"),
    //@SerialName("urn:ietf:params:oauth:grant-type:jwt-bearer") Assertion("urn:ietf:params:oauth:grant-type:jwt-bearer"),
    companion object {
        fun fromValue(value: String): GrantType = checkNotNull(fromValueOrNull(value)) { "No such GrantType [$value]" }
        fun fromValueOrNull(value: String): GrantType? = values().firstOrNull { scope -> scope.value == value }
    }
}