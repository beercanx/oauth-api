package uk.co.baconi.oauth.api.common.scope

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Scope(internal val value: String) {
    @SerialName("basic") Basic("basic"),
    @SerialName("profile::read") ProfileRead("profile::read"),
    @SerialName("profile::write") ProfileWrite("profile::write");
    companion object {
        fun fromValue(value: String): Scope = checkNotNull(fromValueOrNull(value)) { "No such Scope [$value]" }
        fun fromValueOrNull(value: String): Scope? = entries.firstOrNull { scope -> scope.value == value }
    }
}