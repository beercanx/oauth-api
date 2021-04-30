package com.sbgcore.oauth.api.tokens

import org.dizitart.no2.Nitrite
import java.lang.Thread.sleep
import java.time.OffsetDateTime
import java.time.OffsetDateTime.now
import java.time.temporal.ChronoUnit.MILLIS
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * An expiration manager for [AccessToken]'s that are managed by a [Nitrite] database.
 */
class NitriteAccessTokenExpirationManager internal constructor(
    private val threads: ConcurrentHashMap<UUID, Thread>
) {

    /**
     * Restricted access because why does anything other than the [NitriteAccessTokenRepository] need this.
     */
    internal constructor() : this(ConcurrentHashMap<UUID, Thread>())

    /**
     * @param id of the [AccessToken] this will expire.
     * @param expiresAt when the [AccessToken] expires as an [OffsetDateTime].
     * @param expire what to do when this [AccessToken] expires.
     */
    fun expireAfter(id: UUID, expiresAt: OffsetDateTime, expire: (UUID) -> Unit) {

        threads[id] = Thread {

            // Sleep thread until token expires
            try {
                val expiresIn = now().until(expiresAt, MILLIS)
                if(expiresIn > 0) {
                    sleep(expiresIn)
                }
            } catch (exception: InterruptedException) {
                // Sleep was interrupted, either we're shutting down
                // or we've already expired or removed the Access Token
                return@Thread
            }

            // Expire the token
            expire(id)

            // Remove the thread tracker
            threads.remove(id)

        }.also { thread ->
            // Start the thread.
            thread.start()
        }
    }

    /**
     * Removes an expiration tracker and stops it.
     */
    fun remove(id: UUID) {
        threads.remove(id)?.interrupt()
    }
}