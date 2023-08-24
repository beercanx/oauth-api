package uk.co.baconi.oauth.automation.api.config

import java.net.URI

data class Client(val id: ClientId, val secret: ClientSecret, val redirectUri: URI)
