@file:UseSerializers(UUIDSerializer::class)

package uk.co.baconi.oauth.api.authentication

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import uk.co.baconi.oauth.api.common.uuid.UUIDSerializer
import java.util.*

@Serializable
data class CsrfToken(val csrfToken: UUID)
