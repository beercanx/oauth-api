package com.sbgcore.oauth.api.client

import com.sbgcore.oauth.api.enums.WithValue
import com.sbgcore.oauth.api.serializers.ClientIdSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ClientIdSerializer::class)
enum class ClientId(override val value: String) : WithValue {

    ConsumerX("consumer-x"),
    ConsumerY("consumer-y"),
    ConsumerZ("consumer-z"),

    ;

}