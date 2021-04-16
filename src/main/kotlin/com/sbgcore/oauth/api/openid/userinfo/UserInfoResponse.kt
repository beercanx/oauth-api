package com.sbgcore.oauth.api.openid.userinfo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims
 *
 * TODO - Might need replacing with a Map of Claims to Value depending on how the serialisation works.
 *        Aka does it still print null fields for when a client doesn't have access.
 */
@Serializable
data class UserInfoResponse(
    /**
     * Subject - Identifier for the End-User at the Issuer.
     */
    @SerialName("sub") val subject: String,
)
