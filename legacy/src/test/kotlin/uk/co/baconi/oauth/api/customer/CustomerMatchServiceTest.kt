package uk.co.baconi.oauth.api.customer

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class CustomerMatchServiceTest {

    private val repository = mockk<CustomerCredentialRepository>()

    private val checkPassword = mockk<(String, CharArray) -> Boolean>()

    private val underTest = CustomerMatchService(repository, checkPassword)

    @Test
    fun `should return match success when credentials match`(): Unit = runBlocking {

        every { repository.findByUsername("ARTHUR") } returns mockk {
            every { secret } returns "P@55w0rd"
            every { username } returns "ARTHUR"
        }

        every { checkPassword.invoke("P@55w0rd", "P@55w0rd".toCharArray()) } returns true

        assertSoftly(underTest.match("arthur", "P@55w0rd")) {
            it.shouldBeInstanceOf<CustomerMatch.Success>()
            it.username shouldBe "ARTHUR"
        }
    }

    @Test
    fun `should look up credentials with username in uppercase`() : Unit = runBlocking {

        every { repository.findByUsername(any()) } returns null

        listOf("arthur", "Arthur", "ArThUr", "aRtHuR").forEach { username ->
            underTest.match(username, "P@55w0rd") should beInstanceOf<CustomerMatch.Missing>()
        }

        verify(exactly = 4) { repository.findByUsername("ARTHUR") }
    }

    @Test
    fun `should return match failure when credential does not exist`(): Unit = runBlocking {

        every { repository.findByUsername(any()) } returns null

        underTest.match("badger", "P@55w0rd") should beInstanceOf<CustomerMatch.Missing>()
    }

    @Test
    fun `should return match failure when credentials do not match`(): Unit = runBlocking {

        every { repository.findByUsername("COLLIN") } returns mockk {
            every { secret } returns "P@55w0rd"
            every { username } returns "COLLIN"
        }

        every { checkPassword.invoke("P@55w0rd", "password".toCharArray()) } returns false

        underTest.match("collin", "password") should beInstanceOf<CustomerMatch.Mismatched>()
    }
}