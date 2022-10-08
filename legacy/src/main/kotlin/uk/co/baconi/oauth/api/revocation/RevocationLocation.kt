package uk.co.baconi.oauth.api.revocation

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/revoke")
object RevocationLocation