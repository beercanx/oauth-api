package uk.co.baconi.oauth.api.client

import org.dizitart.no2.objects.Id
import java.util.*

data class ClientSecret(@Id val id: UUID, val clientId: ClientId, val secret: String) {
    override fun toString(): String {
        return "ClientSecret(id=$id, clientId=$clientId, secret='REDACTED')"
    }
}