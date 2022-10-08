package uk.co.baconi.oauth.api.common.customer

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.customer.CustomerAuthentication.Failure.Reason
import uk.co.baconi.oauth.api.common.customer.CustomerState.*

class CustomerAuthenticationServiceTest {

    private val credentialRepo = mockk<CustomerCredentialRepository> {
        val username = slot<String>()
        every { findByUsername(capture(username)) } answers { CustomerCredential(username.captured, "hashed") }
    }

    private val statusRepo = mockk<CustomerStatusRepository> {
        val username = slot<String>()
        every { findByUsername(capture(username)) } answers { CustomerStatus(username.captured, Active) }
    }

    private val checkPassword = mockk<(String, CharArray) -> Boolean> {
        every { this@mockk.invoke("hashed", "valid".toCharArray()) } returns true
    }

    private val underTest = CustomerAuthenticationService(credentialRepo, statusRepo, checkPassword)

    @Nested
    inner class Authenticate {

        @Test
        fun `should return failure of missing when no customer credentials found`() {

            every { credentialRepo.findByUsername("missing") } returns null

            assertSoftly(underTest.authenticate("missing", "valid")) {
                shouldBeInstanceOf<CustomerAuthentication.Failure>()
                reason shouldBe Reason.Missing
            }
        }

        @Test
        fun `should return failure of mismatch when customer credentials fail to match`() {

            every { checkPassword.invoke("hashed", "invalid".toCharArray()) } returns false

            assertSoftly(underTest.authenticate("mismatch", "invalid")) {
                shouldBeInstanceOf<CustomerAuthentication.Failure>()
                reason shouldBe Reason.Mismatched
            }
        }

        @Test
        fun `should return failure of missing when no customer status found`() {

            every { statusRepo.findByUsername("no-status") } returns null

            assertSoftly(underTest.authenticate("no-status", "valid")) {
                shouldBeInstanceOf<CustomerAuthentication.Failure>()
                reason shouldBe Reason.Missing
            }
        }

        @Test
        fun `should return failure of closed when the customer status is closed`() {

            every { statusRepo.findByUsername("closed") } returns CustomerStatus("closed", Closed)

            assertSoftly(underTest.authenticate("closed", "valid")) {
                shouldBeInstanceOf<CustomerAuthentication.Failure>()
                reason shouldBe Reason.Closed
            }
        }

        @Test
        fun `should return failure of suspended when the customer status is suspended`() {

            every { statusRepo.findByUsername("suspended") } returns CustomerStatus("suspended", Suspended)

            assertSoftly(underTest.authenticate("suspended", "valid")) {
                shouldBeInstanceOf<CustomerAuthentication.Failure>()
                reason shouldBe Reason.Suspended
            }
        }

        @Test
        fun `should return failure of locked when the customer status is locked`() {

            every { statusRepo.findByUsername("locked") } returns CustomerStatus("locked", Locked)

            assertSoftly(underTest.authenticate("locked", "valid")) {
                shouldBeInstanceOf<CustomerAuthentication.Failure>()
                reason shouldBe Reason.Locked
            }
        }

        @Test
        fun `should return success when the customer status is active`() {

            assertSoftly(underTest.authenticate("aardvark", "valid")) {
                shouldBeInstanceOf<CustomerAuthentication.Success>()
                username shouldBe AuthenticatedUsername("aardvark")
            }
        }
    }
}