package uk.co.baconi.oauth.api.authorization

import io.ktor.locations.*
import uk.co.baconi.oauth.api.client.ClientId
import java.net.URI

@Location("/authorize")
data class AuthorizationLocation(
    val response_type: String? = null,
    val client_id: String? = null,
    val redirect_uri: String? = null,
    val state: String? = null,
    val scope: String? = null,
    val resume: Boolean? = null
)
