package uk.co.baconi.oauth.api.token.introspection

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.scope.Scope.*
import uk.co.baconi.oauth.api.common.token.AccessTokenRepository
import uk.co.baconi.oauth.api.token.introspection.IntrospectionRequest.Valid
import uk.co.baconi.oauth.api.token.introspection.IntrospectionResponse.Inactive
import java.time.Instant.now
import java.time.temporal.ChronoUnit.DAYS
import java.util.*

class IntrospectionServiceTest {

    private val activeToken = accessToken(username = "active")
    private val expiredToken = accessToken(username = "expired", now = now().minus(10, DAYS))
    private val futureToken = accessToken(username = "future", now = now().plus(10, DAYS))
    private val missingToken = UUID.randomUUID()
    private val accessTokenRepository = mockk<AccessTokenRepository> {
        every { findById(activeToken.value) } returns activeToken
        every { findById(expiredToken.value) } returns expiredToken
        every { findById(futureToken.value) } returns futureToken
        every { findById(missingToken) } returns null
    }

    private val underTest = IntrospectionService(accessTokenRepository)

    @Test
    fun `should return a active introspection response for an active access token`() {
        assertSoftly(underTest.introspect(Valid(mockk(), activeToken.value))) {
            shouldBeInstanceOf<IntrospectionResponse.Active>()
            active shouldBe true
            username shouldBe AuthenticatedUsername("active")
            clientId shouldBe ClientId("badger")
            scope shouldContain OpenId
        }
    }

    @Test
    fun `should return an inactive introspection response for a missing access token`() {
        assertSoftly(underTest.introspect(Valid(mockk(), missingToken))) {
            shouldBeInstanceOf<Inactive>()
            active shouldBe false
        }
    }

    @Test
    fun `should return an inactive introspection response for an expired access token`() {
        assertSoftly(underTest.introspect(Valid(mockk(), expiredToken.value))) {
            shouldBeInstanceOf<Inactive>()
            active shouldBe false
        }
    }

    @Test
    fun `should return an inactive introspection response for a future access token`() {
        assertSoftly(underTest.introspect(Valid(mockk(), futureToken.value))) {
            shouldBeInstanceOf<Inactive>()
            active shouldBe false
        }
    }
}