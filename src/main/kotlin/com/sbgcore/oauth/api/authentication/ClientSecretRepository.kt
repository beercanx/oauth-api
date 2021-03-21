package com.sbgcore.oauth.api.authentication

import com.sbgcore.oauth.api.openid.ClientId
import com.sbgcore.oauth.api.enumByValue
import com.sbgcore.oauth.api.Repository
import java.util.*

/**
 * A [Repository] for storing [ClientSecret]'s
 */
interface ClientSecretRepository : Repository<ClientSecret, UUID> {

    /**
     * Find all the [ClientSecret]'s issued to a given [ClientId].
     */
    fun findAllByClientId(clientId: ClientId): Set<ClientSecret>

    /**
     * Find all the [ClientSecret]'s issued to a given [ClientId],
     * but takes a [String] and looks up the [ClientId] first.
     */
    fun findAllByClientId(clientId: String): Set<ClientSecret> {
        val enum = enumByValue<ClientId>(clientId)
        return if(enum == null) {
            emptySet()
        } else {
            findAllByClientId(enum)
        }
    }

}