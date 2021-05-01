package uk.co.baconi.oauth.api.client

import uk.co.baconi.oauth.api.Repository
import uk.co.baconi.oauth.api.enums.deserialise
import java.util.*

/**
 * A [Repository] for storing [ClientSecret]'s
 */
interface ClientSecretRepository : Repository<ClientSecret, UUID> {

    /**
     * Find all the [ClientSecret]'s issued to a given [ClientId].
     */
    fun findAllByClientId(clientId: ClientId): Sequence<ClientSecret>

    /**
     * Find all the [ClientSecret]'s issued to a given [ClientId],
     * but takes a [String] and looks up the [ClientId] first.
     */
    fun findAllByClientId(clientId: String): Sequence<ClientSecret> {
        val enum = deserialise<ClientId>(clientId)
        return if (enum == null) {
            emptySequence()
        } else {
            findAllByClientId(enum)
        }
    }

}