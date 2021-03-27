package com.sbgcore.oauth.api.openid

import com.sbgcore.oauth.api.enums.WithValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class GrantType : WithValue {

    @SerialName("authorization_code") AuthorizationCode {
        override val value = "authorization_code"
    },
    @SerialName("password") Password {
        override val value = "password"
    },
    @SerialName("refresh_token") RefreshToken {
        override val value = "refresh_token"
    },
    @SerialName("urn:ietf:params:oauth:grant-type:jwt-bearer") Assertion {
        override val value = "urn:ietf:params:oauth:grant-type:jwt-bearer"
    },
    @SerialName("sso_token") SsoToken {
        override val value = "sso_token"
    },

    ;

}