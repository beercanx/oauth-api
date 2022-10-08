package uk.co.baconi.oauth.api.common.client

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ClientType(internal val value: String) {
    @SerialName("confidential") Confidential("confidential"),
    @SerialName("public") Public("public");
    companion object {
        fun fromValue(value: String): ClientType = checkNotNull(fromValueOrNull(value)) { "No such ClientType [$value]" }
        fun fromValueOrNull(value: String): ClientType? = values().firstOrNull { type -> type.value == value }
    }
}