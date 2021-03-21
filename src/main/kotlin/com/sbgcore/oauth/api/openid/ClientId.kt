package com.sbgcore.oauth.api.openid

import com.sbgcore.oauth.api.enums.WithValue

enum class ClientId(override val value: String) : WithValue {

    ConsumerX("consumer-x"),
    ConsumerZ("consumer-z"),

    ;

}