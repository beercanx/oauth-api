package uk.co.baconi.oauth.api.common.ktor.auth

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.client.ConfidentialClient
import uk.co.baconi.oauth.api.common.client.PublicClient

class ExtractClientTest {

    @Test
    fun `should extract a confidential client if one is present`() {

        val client = mockk<ConfidentialClient>()

        val call = mockk<ApplicationCall> {
            every { principal<ConfidentialClient>() } returns client
        }

        call.extractClient<ConfidentialClient>() shouldBe client
    }

    @Test
    fun `should throw an exception if a confidential client is not present`() {

        val call = mockk<ApplicationCall> {
            every { principal<ConfidentialClient>() } returns null
        }

        val exception = shouldThrow<IllegalStateException> {
            call.extractClient<ConfidentialClient>()
        }

        exception.message shouldContain "ConfidentialClient should not be null"
    }

    @Test
    fun `should extract a public client if one is present`() {

        val client = mockk<PublicClient>()

        val call = mockk<ApplicationCall> {
            every { principal<PublicClient>() } returns client
        }

        call.extractClient<PublicClient>() shouldBe client
    }

    @Test
    fun `should throw an exception if a public client is not present`() {

        val call = mockk<ApplicationCall> {
            every { principal<PublicClient>() } returns null
        }

        val exception = shouldThrow<IllegalStateException> {
            call.extractClient<PublicClient>()
        }

        exception.message shouldContain "PublicClient should not be null"
    }
}