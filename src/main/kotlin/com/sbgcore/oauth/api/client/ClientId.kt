package com.sbgcore.oauth.api.client

import com.sbgcore.oauth.api.enums.WithValue

enum class ClientId(override val value: String) : WithValue {

    ConsumerX("consumer-x"),
    ConsumerY("consumer-y"),
    ConsumerZ("consumer-z"),

    ;

}