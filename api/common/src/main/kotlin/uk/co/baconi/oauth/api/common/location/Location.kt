package uk.co.baconi.oauth.api.common.location

import com.typesafe.config.ConfigFactory
import io.ktor.http.*

/**
 * Locations of each major base endpoint.
 */
enum class Location(val baseUrl: Url) {

    Assets("assets"),
    Authentication("authentication"),
    Authorisation("authorisation"),
    SessionInfo("session-info"),
    Token("token"),
    TokenIntrospection("token-introspection"),
    TokenRevocation("token-revocation"),
    UserInfo("user-info"),
    WellKnown("well-known");

    constructor(name: String) : this(
        Url(config.getString(name)),
    )

    companion object {
        private val config = ConfigFactory.load().getConfig("uk.co.baconi.oauth.api.locations")
    }
}