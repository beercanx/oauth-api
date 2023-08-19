package uk.co.baconi.oauth.api.common

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.beEmpty
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.ContentType.Application.FormUrlEncoded
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.http.HttpStatusCode.Companion.Unauthorized
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.CommonModule.common
import uk.co.baconi.oauth.api.common.client.ClientSecretService
import uk.co.baconi.oauth.api.common.client.ConfidentialClient
import uk.co.baconi.oauth.api.common.client.PublicClient
import uk.co.baconi.oauth.api.common.ktor.auth.authenticate
import uk.co.baconi.oauth.api.common.token.AccessToken
import uk.co.baconi.oauth.api.common.token.AccessTokenService

class AuthenticationModuleTest : AuthenticationModule {

    override val clientSecretService = mockk<ClientSecretService>()
    override val accessTokenService = mockk<AccessTokenService>()

    private inline fun <reified T : Principal> underTest(crossinline block: suspend ApplicationTestBuilder.() -> Unit) {
        testApplication {
            application {
                common()
                authentication()
            }
            routing {
                route("/") {
                    authenticate(T::class) {
                        contentType(FormUrlEncoded) {
                            post {
                                call.respond(OK, "OK")
                            }
                        }
                    }
                }
            }
            block()
        }
    }

    @Nested
    inner class ConfidentialClientAuthentication {

        @Test
        fun `should return 401 on missing authentication`() = underTest<ConfidentialClient> {

            assertSoftly(client.post("/") {
                contentType(FormUrlEncoded)
            }) {
                status shouldBe Unauthorized
                bodyAsText() should beEmpty()
            }

            verify(exactly = 0) { clientSecretService.authenticate(any(), any()) }
        }

        @Test
        fun `should return 401 on invalid authentication`() = underTest<ConfidentialClient> {

            every { clientSecretService.authenticate(any(), any()) } returns null

            assertSoftly(client.post("/") {
                basicAuth("aardvark", "badger")
                contentType(FormUrlEncoded)
            }) {
                status shouldBe Unauthorized
                bodyAsText() should beEmpty()
            }

            verify { clientSecretService.authenticate("aardvark", "badger") }
        }

        @Test
        fun `should return 200 on valid authentication`() = underTest<ConfidentialClient> {

            every { clientSecretService.authenticate("aardvark", "badger") } returns mockk<ConfidentialClient>()

            assertSoftly(client.post("/") {
                basicAuth("aardvark", "badger")
                contentType(FormUrlEncoded)
            }) {
                status shouldBe OK
                bodyAsText() shouldBe "OK"
            }

            verify { clientSecretService.authenticate("aardvark", "badger") }
        }
    }

    @Nested
    inner class PublicClientAuthentication {

        @Test
        fun `should return 401 on missing authentication`() = underTest<PublicClient> {

            assertSoftly(client.post("/") {
                contentType(FormUrlEncoded)
            }) {
                status shouldBe Unauthorized
                bodyAsText() should beEmpty()
            }

            verify(exactly = 0) { clientSecretService.authenticate(any()) }
        }

        @Test
        fun `should return 401 on invalid authentication`() = underTest<PublicClient> {

            every { clientSecretService.authenticate(any()) } returns null

            assertSoftly(client.post("/") {
                contentType(FormUrlEncoded)
                setBody("client_id=aardvark")
            }) {
                status shouldBe Unauthorized
                bodyAsText() should beEmpty()
            }

            verify { clientSecretService.authenticate("aardvark") }
        }

        @Test
        fun `should return 200 on valid authentication`() = underTest<PublicClient> {

            every { clientSecretService.authenticate("aardvark") } returns mockk<PublicClient>()

            assertSoftly(client.post("/") {
                contentType(FormUrlEncoded)
                setBody("client_id=aardvark")
            }) {
                status shouldBe OK
                bodyAsText() shouldBe "OK"
            }

            verify { clientSecretService.authenticate("aardvark") }
        }
    }

    @Nested
    inner class AccessTokenAuthentication {

        @Test
        fun `should return 401 on missing authentication`() = underTest<AccessToken> {

            assertSoftly(client.post("/") {
                contentType(FormUrlEncoded)
            }) {
                status shouldBe Unauthorized
                bodyAsText() should beEmpty()
            }

            verify(exactly = 0) { accessTokenService.authenticate(any<String>()) }
        }

        @Test
        fun `should return 401 on invalid authentication`() = underTest<AccessToken> {

            every { accessTokenService.authenticate(any<String>()) } returns null

            assertSoftly(client.post("/") {
                bearerAuth("aardvark")
                contentType(FormUrlEncoded)
            }) {
                status shouldBe Unauthorized
                bodyAsText() should beEmpty()
            }

            verify { accessTokenService.authenticate("aardvark") }
        }

        @Test
        fun `should return 200 on valid authentication`() = underTest<AccessToken> {

            every { accessTokenService.authenticate("aardvark") } returns mockk<AccessToken>()

            assertSoftly(client.post("/") {
                bearerAuth("aardvark")
                contentType(FormUrlEncoded)
            }) {
                status shouldBe OK
                bodyAsText() shouldBe "OK"
            }

            verify { accessTokenService.authenticate("aardvark") }
        }
    }
}