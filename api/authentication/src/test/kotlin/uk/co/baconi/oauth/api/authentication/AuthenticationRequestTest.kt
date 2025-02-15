package uk.co.baconi.oauth.api.authentication

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContainInOrder
import org.junit.jupiter.api.Test

class AuthenticationRequestTest {

    @Test
    fun `Raw should not include customer password in toString`() {
        assertSoftly(
            AuthenticationRequest.Raw(
                username = "aardvark",
                password = charArrayOf('1','2','3','4','5','6'),
                csrfToken = "badger"
            ).toString()
        ) {
            shouldNotContainInOrder("1", "2", "3", "4", "5", "6")
            shouldContain("username='aardvark'")
            shouldContain("password='REDACTED'")
            shouldContain("csrfToken='badger'")
        }
    }

    @Test
    fun `Valid should not include customer password in toString`() {
        assertSoftly(
            AuthenticationRequest.Valid(
                username = "aardvark",
                password = charArrayOf('1','2','3','4','5','6')
            ).toString()
        ) {
            shouldNotContainInOrder("1", "2", "3", "4", "5", "6")
            shouldContain("username='aardvark'")
            shouldContain("password='REDACTED'")
        }
    }
}