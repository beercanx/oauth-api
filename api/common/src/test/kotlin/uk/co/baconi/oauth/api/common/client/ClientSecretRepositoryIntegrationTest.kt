package uk.co.baconi.oauth.api.common.client

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.sequences.beEmpty
import io.kotest.matchers.sequences.shouldContain
import io.kotest.matchers.sequences.shouldHaveCount
import io.kotest.matchers.sequences.shouldNotContain
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID.fromString
import java.util.UUID.randomUUID

class ClientSecretRepositoryIntegrationTest {

    private val underTest = ClientSecretRepository()

    private val consumerXClientSecret = ClientSecret(
        id = fromString("ab998101-5acb-4f9c-9a31-0136daa01ec7"),
        clientId = ClientId("consumer-x"),
        hashedSecret = "\$2a\$06\$3hCQBc6v.oqlCqpjhuZZz.wdGuZbT/bjFK5makOayne3/dsErUMru"
    )

    @Nested
    inner class FindById {

        @Test
        fun `should allow finding by uuid`() {
            val uuid = fromString("956f05cc-4011-4d73-b7ae-d43d7cc5bd24")
            underTest.findById(uuid) shouldBe ClientSecret(
                id = uuid,
                clientId = ClientId("consumer-z"),
                hashedSecret = "\$2a\$06\$wSH/osEXHWgsviylp5PgTO4ns9oBRcbtVGf8dn/o0kNRwzj3X7nyy"
            )
        }

        @Test
        fun `should return null when no record exists`() {
            underTest.findById(randomUUID()) shouldBe null
        }
    }

    @Nested
    inner class FindAllByClientId {

        @Test
        fun `should return singleton collection when there's just one record`() {
            assertSoftly(underTest.findAllByClientId(ClientId("consumer-x"))) {
                it shouldHaveCount 1
                it shouldContain consumerXClientSecret
            }
        }

        @Test
        fun `should return all secrets for the given client id`() {
            assertSoftly(underTest.findAllByClientId(ClientId("consumer-z"))) {
                it shouldHaveCount 2
                it shouldNotContain consumerXClientSecret
            }
        }

        @Test
        fun `should return an empty sequence when no record exists`() {
            underTest.findAllByClientId(ClientId("consumer-y")) should beEmpty()
        }
    }

    @Nested
    inner class FindAllByClientIdAsString {

        @Test
        fun `should return singleton collection when there's just one record`() {
            assertSoftly(underTest.findAllByClientId("consumer-x")) {
                it shouldHaveCount 1
                it shouldContain consumerXClientSecret
            }
        }

        @Test
        fun `should return all secrets for the given client id`() {
            assertSoftly(underTest.findAllByClientId("consumer-z")) {
                it shouldHaveCount 2
                it shouldNotContain consumerXClientSecret
            }
        }

        @Test
        fun `should return empty sequence when no record exists`() {
            underTest.findAllByClientId("consumer-y") should beEmpty()
        }

        @Test
        fun `should return empty sequence when no client exists with that id`() {
            underTest.findAllByClientId("aardvark") should beEmpty()
        }
    }
}