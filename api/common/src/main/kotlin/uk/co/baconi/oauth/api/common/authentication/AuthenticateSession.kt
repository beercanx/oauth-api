@file:UseSerializers(UUIDSerializer::class)

package uk.co.baconi.oauth.api.common.authentication

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import uk.co.baconi.oauth.api.common.uuid.UUIDSerializer
import java.util.*

@Serializable
data class AuthenticateSession(val csrfToken: UUID)