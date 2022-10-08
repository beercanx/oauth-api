package uk.co.baconi.oauth.api.common.scope

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Scope(internal val value: String) {
    @SerialName("openid") OpenId("openid"),
    @SerialName("profile::read") ProfileRead("profile::read"),
    @SerialName("profile::write") ProfileWrite("profile::write");
    companion object {
        fun fromValue(value: String) = checkNotNull(fromValueOrNull(value)) { "No such Scope with value [$value]" }
        fun fromValueOrNull(value: String): Scope? = values().firstOrNull { scope -> scope.value == value }
    }
}