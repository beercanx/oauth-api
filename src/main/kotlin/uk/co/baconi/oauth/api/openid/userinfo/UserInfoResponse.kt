package uk.co.baconi.oauth.api.openid.userinfo

import uk.co.baconi.oauth.api.openid.Claims

/**
 * https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims
 */
typealias UserInfoResponse = Map<Claims, Any?>
