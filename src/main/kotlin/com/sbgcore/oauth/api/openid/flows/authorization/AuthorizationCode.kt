package com.sbgcore.oauth.api.openid.flows.authorization

import org.dizitart.no2.objects.Id
import java.time.OffsetDateTime
import java.util.*

// TODO - Expand with consumer issued to details
data class AuthorizationCode(@Id val value: UUID, val issuedAt: OffsetDateTime)
