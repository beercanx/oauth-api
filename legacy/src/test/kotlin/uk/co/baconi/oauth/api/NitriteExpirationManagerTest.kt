package uk.co.baconi.oauth.api

import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.time.OffsetDateTime.now
import java.time.temporal.ChronoUnit.MILLIS
import java.time.temporal.ChronoUnit.HOURS
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit.SECONDS

@Timeout(10, unit = SECONDS)
class NitriteExpirationManagerTest {

    private val threads = ConcurrentHashMap<String, Thread>()
    private val underTest = NitriteExpirationManager(threads)

    @Test
    fun `should expire token after the given datetime`() {

        val latch = CountDownLatch(1)
        val id = UUID.randomUUID().toString()
        val expireAt = now().plus(50, MILLIS)
        val expire: (String) -> Unit = mockk {
            every { this@mockk.invoke(any()) } answers {

                // should add the thread to the map
                threads[id] shouldNot beNull()

                latch.countDown()
            }
        }

        underTest.expireAfter(id, expireAt, expire)

        latch.await()

        // should expire token after the given datetime
        verify { expire.invoke(id) }

        // should remove the thread from the map on expiration
        threads[id] should beNull()
    }

    @Test
    fun `should stop and remove the thread when requested`() {

        val id = UUID.randomUUID().toString()
        val expireAt = now().plus(1, HOURS)
        val expire: (String) -> Unit = mockk {
            every { this@mockk.invoke(any()) } returns Unit
        }

        underTest.expireAfter(id, expireAt, expire)

        threads[id] shouldNot beNull()

        // Should not have expired yet
        verify(exactly = 0) { expire.invoke(any()) }

        underTest.remove(id)

        threads[id] should beNull()

        // Should not bother trying to expire when requested to stop and remove
        verify(exactly = 0) { expire.invoke(any()) }
    }
}