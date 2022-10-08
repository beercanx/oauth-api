package uk.co.baconi.oauth.api.tokens

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.date.*
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.client.ClientId.ConsumerZ
import uk.co.baconi.oauth.api.openid.Scopes.OpenId

class AccessTokenServiceTest {

    private val accessTokenSlot = slot<AccessToken>()
    private val repository = mockk<AccessTokenRepository> {
        every { insert(capture(accessTokenSlot)) } returns Unit
    }

    private val underTest = AccessTokenService(repository)

    @Test
    fun `should insert the issued access token into the repository`() {

        val result = underTest.issue(
            username = "aardvark",
            clientId = ConsumerZ,
            scopes = setOf(OpenId)
        )

        accessTokenSlot.captured shouldBe result
    }

    @Test
    fun `should issue the access token with sensible date values`() {

        val result = underTest.issue("aardvark", ConsumerZ, emptySet())

        assertSoftly(result) {
            issuedAt.shouldBeToday()
            expiresAt shouldBeAfter issuedAt
            notBefore shouldBeBefore issuedAt
        }
    }

    @Test
    fun `should issue the access token with an id that's different to the value`() {

        val result = underTest.issue("aardvark", ConsumerZ, emptySet())

        result.id shouldNotBe result.value
    }
}