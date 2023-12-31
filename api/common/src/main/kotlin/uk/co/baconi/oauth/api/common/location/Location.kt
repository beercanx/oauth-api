package uk.co.baconi.oauth.api.common.location

import com.typesafe.config.ConfigFactory
import io.ktor.http.*

private val config = ConfigFactory.load().getConfig("uk.co.baconi.oauth.api.locations")

/**
 * Locations of each major base endpoint. TODO - Replace with something more sensible, domains change, so do IPs
 */
enum class Location(val baseUrl: Url) {

    Assets("assets"),
    Authentication("authentication"),
    Authorisation("authorisation"),
    SessionInfo("session-info"),
    Token("token"),
    TokenIntrospection("token-introspection"),
    TokenRevocation("token-revocation");

    constructor(name: String) : this(
        Url(config.getString(name)),
    )
}