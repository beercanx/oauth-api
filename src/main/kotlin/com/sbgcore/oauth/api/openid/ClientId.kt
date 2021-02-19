package com.sbgcore.oauth.api.openid

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ClientId : SerializableEnum {

    @SerialName("consumer-x") ConsumerX,
    @SerialName("consumer-z") ConsumerZ,
    ;

    // TODO - Decide if we even need this
    val value: String = getSerialName()
}