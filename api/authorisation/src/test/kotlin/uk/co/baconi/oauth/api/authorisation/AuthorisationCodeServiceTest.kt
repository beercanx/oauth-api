package uk.co.baconi.oauth.api.authorisation

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.date.shouldBeAfter
import io.kotest.matchers.date.shouldBeBefore
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.authorisation.AuthorisationRequest.Basic
import uk.co.baconi.oauth.api.authorisation.AuthorisationRequest.PKCE
import uk.co.baconi.oauth.api.common.authentication.AuthenticatedUsername
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCode
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeRepository
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationResponseType.Code
import uk.co.baconi.oauth.api.common.authorisation.CodeChallenge
import uk.co.baconi.oauth.api.common.authorisation.CodeChallengeMethod
import uk.co.baconi.oauth.api.common.authorisation.CodeChallengeMethod.S256
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.scope.Scope

import java.time.Instant.now
import java.time.temporal.ChronoUnit.MINUTES
import java.time.temporal.ChronoUnit.SECONDS

class AuthorisationCodeServiceTest {

    private val repository = mockk<AuthorisationCodeRepository>(relaxed = true)
    private val underTest = AuthorisationCodeService(repository)

    @Test
    fun `should be able to issue a new authorisation code`() {

        val request = Basic(Code, ClientId("badger"), "https://localhost", "state-1-2-3", setOf(Scope("basic")))

        val result = underTest.issue(request, AuthenticatedUsername("aardvark"))

        assertSoftly(result) {

            shouldBeInstanceOf<AuthorisationCode.Basic>()

            value.shouldNotBeNull()
            clientId shouldBe ClientId("badger")
            redirectUri shouldBe "https://localhost"
            state shouldBe "state-1-2-3"
            scopes shouldHaveSingleElement Scope("basic")

            issuedAt.shouldBeBefore(now().plus(5, SECONDS))
            expiresAt.shouldBeAfter(now())
            expiresAt.shouldBeBefore(now().plus(10, MINUTES))

            username shouldBe AuthenticatedUsername("aardvark")
        }

        verify { repository.insert(eq(result)) }
    }

    @Test
    fun `should be able to issue a new pkce authorisation code`() {

        val base = Basic(Code, ClientId("badger"), "https://localhost", "state-1-2-3", setOf(Scope("basic")))
        val request = PKCE(base, CodeChallenge("code-challenge-a-b-c"), S256)

        val result = underTest.issue(request, AuthenticatedUsername("aardvark"))

        assertSoftly(result) {

            shouldBeInstanceOf<AuthorisationCode.PKCE>()

            value.shouldNotBeNull()
            clientId shouldBe ClientId("badger")
            redirectUri shouldBe "https://localhost"
            state shouldBe "state-1-2-3"
            scopes shouldHaveSingleElement Scope("basic")

            issuedAt.shouldBeBefore(now().plus(5, SECONDS))
            expiresAt.shouldBeAfter(now())
            expiresAt.shouldBeBefore(now().plus(10, MINUTES))

            username shouldBe AuthenticatedUsername("aardvark")

            codeChallengeMethod shouldBe S256
            codeChallenge shouldBe CodeChallenge("code-challenge-a-b-c")
        }

        verify { repository.insert(eq(result)) }
    }
}
