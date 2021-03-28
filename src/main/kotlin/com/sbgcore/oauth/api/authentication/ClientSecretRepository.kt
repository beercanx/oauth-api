package com.sbgcore.oauth.api.authentication

import com.sbgcore.oauth.api.enums.enumByValue
import com.sbgcore.oauth.api.Repository
import com.sbgcore.oauth.api.client.ClientId
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
        val enum = enumByValue<ClientId>(clientId)
        return if(enum == null) {
            emptySequence()
        } else {
            findAllByClientId(enum)
        }
    }

}