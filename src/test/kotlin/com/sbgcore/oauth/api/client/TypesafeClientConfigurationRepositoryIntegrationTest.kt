package com.sbgcore.oauth.api.client

import com.sbgcore.oauth.api.client.ClientId.*
import com.sbgcore.oauth.api.client.ClientType.Public
import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.ktor.http.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TypesafeClientConfigurationRepositoryIntegrationTest {

    private val repository = spyk(
        ConfigFactory.parseString(
            """
                # Working example
                consumer-y: {
                    type: Public,
                    redirectUrls: [
                        "uk.co.baconi.consumer-y://callback",
                    ]
                },
            """.trimIndent()
        )
    )

    private val underTest: ClientConfigurationRepository = spyk(TypesafeClientConfigurationRepository(repository))

    @Nested
    inner class Insert {

        @Test
        fun `should throw an exception as its not supported`() {

            shouldThrow<IllegalStateException> {
                underTest.insert(mockk())
            } shouldHaveMessage "Insert operation is not supported"
        }
    }

    @Nested
    inner class Delete {

        @Test
        fun `should throw an exception as its not supported`() {

            shouldThrow<IllegalStateException> {
                underTest.delete(mockk())
            } shouldHaveMessage "Delete operation is not supported"
        }
    }

    @Nested
    inner class FindById {

        @Test
        fun `should return configuration if its defined in config`() {

            underTest.findById(ConsumerY) shouldBe ClientConfiguration(
                id = ConsumerY,
                type = Public,
                redirectUrls = setOf(Url("uk.co.baconi.consumer-y://callback"))
            )
        }

        @Test
        fun `should return null if the configuration does not exist`() {

            underTest.findById(ConsumerX) shouldBe null
        }

        @Test
        fun `should throw exception on incorrect configuration`() {

            every { repository.hasPath(any()) } returns true

            every { repository.getConfig(ConsumerZ.value) } returns ConfigFactory.parseString(
                """
                    type: Aardvark
                """.trimIndent()
            )

            every { repository.getConfig(ConsumerX.value) } returns ConfigFactory.parseString(
                """
                    type: Public,
                    redirectUrls: true
                """.trimIndent()
            )

            every { repository.getConfig(ConsumerY.value) } returns ConfigFactory.empty()

            assertSoftly {

                shouldThrow<ConfigException.BadValue> {
                    underTest.findById(ConsumerZ)
                }

                shouldThrow<ConfigException.WrongType> {
                    underTest.findById(ConsumerX)
                }

                shouldThrow<ConfigException.Missing> {
                    underTest.findById(ConsumerY)
                }
            }
        }
    }

    @Nested
    inner class FindByClientId {

        @Test
        fun `should call findById`() {

            underTest.findByClientId(ConsumerY) shouldNotBe null

            verify { underTest.findById(ConsumerY) }
        }
    }

    @Nested
    inner class FindByClientIdAsString {

        @Test
        fun `should call findById`() {

            underTest.findByClientId(ConsumerY.value) shouldNotBe null

            verify { underTest.findById(ConsumerY) }
        }

        @Test
        fun `should return null if client does not exist`() {

            underTest.findByClientId("aardvark")

            verify(exactly = 0) { underTest.findById(any()) }
        }
    }
}