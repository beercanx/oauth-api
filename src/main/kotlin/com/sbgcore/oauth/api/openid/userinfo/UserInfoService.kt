package com.sbgcore.oauth.api.openid.userinfo

import com.sbgcore.oauth.api.tokens.AccessToken
import com.sbgcore.oauth.api.tokens.AccessTokenRepository
import java.time.OffsetDateTime.now
import java.util.*

class UserInfoService {

    // https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims
    fun getUserInfo(accessToken: AccessToken): UserInfoResponse {
        // TODO - Convert to using claims issues
        return UserInfoResponse(
            subject = accessToken.username,
        )
    }
}