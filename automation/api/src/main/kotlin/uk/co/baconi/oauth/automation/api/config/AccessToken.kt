package uk.co.baconi.oauth.automation.api.config

import java.util.UUID

data class AccessToken(override val value: String) : Token {
    constructor(uuid: UUID) : this(uuid.toString())
}
