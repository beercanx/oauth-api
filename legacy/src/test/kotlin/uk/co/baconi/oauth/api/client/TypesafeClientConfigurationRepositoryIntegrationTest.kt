package uk.co.baconi.oauth.api.client

import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.client.ClientId.*
import uk.co.baconi.oauth.api.client.ClientType.Public
import uk.co.baconi.oauth.api.enums.serialise
import uk.co.baconi.oauth.api.scopes.Scopes.OpenId

class TypesafeClientConfigurationRepositoryIntegrationTest {

    private val repository = spyk(
        ConfigFactory.parseString(
            """
                # Working example
                consumer-y: {
                    type: Public,
                    redirectUris: [
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
    inner class ToUrls {

        init {
            every { repository.hasPath(any()) } returns true
        }

        @Test
        fun `should be able to handle missing entry`() {

            every { repository.getConfig(ConsumerZ.serialise()) } returns ConfigFactory.parseString(
                """
                    type: Public
                """.trimIndent()
            )

            assertSoftly(underTest.findById(ConsumerZ)) {
                shouldNotBeNull()
                redirectUris should beEmpty()
            }
        }

        @Test
        fun `should be able to handle null entry`() {

            every { repository.getConfig(ConsumerZ.serialise()) } returns ConfigFactory.parseString(
                """
                    type: Public,
                    redirectUris: null
                """.trimIndent()
            )

            assertSoftly(underTest.findById(ConsumerZ)) {
                shouldNotBeNull()
                redirectUris should beEmpty()
            }
        }

        @Test
        fun `should be able to handle empty entry`() {

            every { repository.getConfig(ConsumerZ.serialise()) } returns ConfigFactory.parseString(
                """
                    type: Public,
                    redirectUris: []
                """.trimIndent()
            )

            assertSoftly(underTest.findById(ConsumerZ)) {
                shouldNotBeNull()
                redirectUris should beEmpty()
            }
        }

        @Test
        fun `should be able to handle valid uri entry`() {

            every { repository.getConfig(ConsumerZ.serialise()) } returns ConfigFactory.parseString(
                """
                    type: Public,
                    redirectUris: ["uk.co.consumer-z://callback"]
                """.trimIndent()
            )

            assertSoftly(underTest.findById(ConsumerZ)) {
                shouldNotBeNull()
                redirectUris shouldContain "uk.co.consumer-z://callback"
            }
        }
    }

    @Nested
    inner class ToScopes {

        init {
            every { repository.hasPath(any()) } returns true
        }

        @Test
        fun `should be able to handle missing entry`() {

            every { repository.getConfig(ConsumerZ.serialise()) } returns ConfigFactory.parseString(
                """
                    type: Public
                """.trimIndent()
            )

            assertSoftly(underTest.findById(ConsumerZ)) {
                shouldNotBeNull()
                allowedScopes should beEmpty()
            }
        }

        @Test
        fun `should be able to handle null entry`() {

            every { repository.getConfig(ConsumerZ.serialise()) } returns ConfigFactory.parseString(
                """
                    type: Public,
                    allowedScopes: null
                """.trimIndent()
            )

            assertSoftly(underTest.findById(ConsumerZ)) {
                shouldNotBeNull()
                allowedScopes should beEmpty()
            }
        }

        @Test
        fun `should be able to handle empty entry`() {

            every { repository.getConfig(ConsumerZ.serialise()) } returns ConfigFactory.parseString(
                """
                    type: Public,
                    allowedScopes: []
                """.trimIndent()
            )

            assertSoftly(underTest.findById(ConsumerZ)) {
                shouldNotBeNull()
                allowedScopes should beEmpty()
            }
        }

        @Test
        fun `should be able to handle invalid scope entry`() {

            every { repository.getConfig(ConsumerZ.serialise()) } returns ConfigFactory.parseString(
                """
                    type: Public,
                    allowedScopes: [aardvark]
                """.trimIndent()
            )

            assertSoftly(underTest.findById(ConsumerZ)) {
                shouldNotBeNull()
                allowedScopes should beEmpty()
            }
        }

        @Test
        fun `should be able to handle valid scope entry`() {

            every { repository.getConfig(ConsumerZ.serialise()) } returns ConfigFactory.parseString(
                """
                    type: Public,
                    allowedScopes: [openid]
                """.trimIndent()
            )

            assertSoftly(underTest.findById(ConsumerZ)) {
                shouldNotBeNull()
                allowedScopes shouldContainExactly setOf(OpenId)
            }
        }
    }

    @Nested
    inner class FindById {

        @Test
        fun `should return configuration if its defined in config`() {

            underTest.findById(ConsumerY) shouldBe ClientConfiguration(
                id = ConsumerY,
                type = Public,
                redirectUris = setOf("uk.co.baconi.consumer-y://callback"),
                allowedScopes = emptySet(),
                allowedResponseTypes = emptySet(),
            )
        }

        @Test
        fun `should return null if the configuration does not exist`() {

            underTest.findById(ConsumerX) shouldBe null
        }

        @Test
        fun `should throw exception on incorrect configuration`() {

            every { repository.hasPath(any()) } returns true

            every { repository.getConfig(ConsumerZ.serialise()) } returns ConfigFactory.parseString(
                """
                    type: Aardvark
                """.trimIndent()
            )

            every { repository.getConfig(ConsumerX.serialise()) } returns ConfigFactory.parseString(
                """
                    type: Public,
                    redirectUris: true
                """.trimIndent()
            )

            every { repository.getConfig(ConsumerY.serialise()) } returns ConfigFactory.empty()

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

            underTest.findByClientId(ConsumerY) shouldNotBe null

            verify { underTest.findById(ConsumerY) }
        }

        @Test
        fun `should return null if client does not exist`() {

            underTest.findByClientId("aardvark")

            verify(exactly = 0) { underTest.findById(any()) }
        }
    }
}