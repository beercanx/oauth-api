@file:UseSerializers(UUIDSerializer::class)

package uk.co.baconi.oauth.api.client

import uk.co.baconi.oauth.api.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.dizitart.no2.objects.Id
import java.util.*

@Serializable
data class ClientSecret(@Id val id: UUID, val clientId: ClientId, val secret: String) {
    override fun toString(): String {
        return "ClientSecret(id=$id, clientId=$clientId)"
    }
}