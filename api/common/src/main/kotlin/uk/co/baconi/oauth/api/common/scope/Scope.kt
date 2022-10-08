package uk.co.baconi.oauth.api.common.scope

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Scope(val value: String) {
    @SerialName("openid") OpenID("openid"),
    @SerialName("profile::read") ProfileRead("profile::read"),
    @SerialName("profile::write") ProfileWrite("profile::write");
    companion object {
        fun fromValue(value: String): Scope = values().single { scope -> scope.value == value }
    }
}