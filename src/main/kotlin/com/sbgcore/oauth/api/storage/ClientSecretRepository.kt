package com.sbgcore.oauth.api.storage

import com.sbgcore.oauth.api.authentication.ClientSecret
import com.sbgcore.oauth.api.openid.ClientId
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
        val enum: ClientId? = enumValues<ClientId>().firstOrNull { it.value == clientId }
        return if(enum == null) {
            emptySet()
        } else {
            findAllByClientId(enum)
        }
    }

}