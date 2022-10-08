package uk.co.baconi.oauth.api

import org.dizitart.no2.Nitrite
import java.lang.Thread.sleep
import java.time.OffsetDateTime
import java.time.OffsetDateTime.now
import java.time.temporal.ChronoUnit.MILLIS
import java.util.concurrent.ConcurrentHashMap

/**
 * An expiration manager for entities that are managed by a [Nitrite] database.
 */
class NitriteExpirationManager<ID : Any> internal constructor(
    private val threads: ConcurrentHashMap<ID, Thread>
) {

    /**
     * Restricted access because why does anything other than the [Nitrite] based [Repository] need this.
     */
    internal constructor() : this(ConcurrentHashMap<ID, Thread>())

    /**
     * @param id of the entity this will expire.
     * @param expiresAt when the entity expires as an [OffsetDateTime].
     * @param expire what to do when this entity expires.
     */
    fun expireAfter(id: ID, expiresAt: OffsetDateTime, expire: (ID) -> Unit) {

        threads[id] = Thread {

            // Sleep thread until token expires
            try {
                val expiresIn = now().until(expiresAt, MILLIS)
                if (expiresIn > 0) {
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
    fun remove(value: ID) {
        threads.remove(value)?.interrupt()
    }
}