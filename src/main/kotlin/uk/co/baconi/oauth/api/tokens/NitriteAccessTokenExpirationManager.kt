package uk.co.baconi.oauth.api.tokens

import org.dizitart.no2.Nitrite
import java.lang.Thread.sleep
import java.time.OffsetDateTime
import java.time.OffsetDateTime.now
import java.time.temporal.ChronoUnit.MILLIS
import java.util.concurrent.ConcurrentHashMap

/**
 * An expiration manager for [AccessToken]'s that are managed by a [Nitrite] database.
 */
class NitriteAccessTokenExpirationManager internal constructor(
    private val threads: ConcurrentHashMap<String, Thread>
) {

    /**
     * Restricted access because why does anything other than the [NitriteAccessTokenRepository] need this.
     */
    internal constructor() : this(ConcurrentHashMap<String, Thread>())

    /**
     * @param value of the [AccessToken] this will expire.
     * @param expiresAt when the [AccessToken] expires as an [OffsetDateTime].
     * @param expire what to do when this [AccessToken] expires.
     */
    fun expireAfter(value: String, expiresAt: OffsetDateTime, expire: (String) -> Unit) {

        threads[value] = Thread {

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
            expire(value)

            // Remove the thread tracker
            threads.remove(value)

        }.also { thread ->
            // Start the thread.
            thread.start()
        }
    }

    /**
     * Removes an expiration tracker and stops it.
     */
    fun remove(value: String) {
        threads.remove(value)?.interrupt()
    }
}