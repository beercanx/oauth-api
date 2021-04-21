package com.sbgcore.oauth.api.openid

import com.sbgcore.oauth.api.enums.WithValue
import com.sbgcore.oauth.api.serializers.EnumWithValueSerializer
import com.sbgcore.oauth.api.serializers.GrantTypeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(with = GrantTypeSerializer::class)
enum class GrantType(override val value: String) : WithValue {

    AuthorizationCode("authorization_code"),
    Password("password"),
    RefreshToken("refresh_token"),
    Assertion("urn:ietf:params:oauth:grant-type:jwt-bearer"),

    ;

}