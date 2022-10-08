package uk.co.baconi.oauth.api.userinfo

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/userinfo")
class UserInfoLocation