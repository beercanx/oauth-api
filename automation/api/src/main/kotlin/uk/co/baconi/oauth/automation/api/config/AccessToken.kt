package uk.co.baconi.oauth.automation.api.config

import java.util.UUID

@JvmInline
value class AccessToken(val value: String) {
    constructor(uuid: UUID) : this(uuid.toString())
}
