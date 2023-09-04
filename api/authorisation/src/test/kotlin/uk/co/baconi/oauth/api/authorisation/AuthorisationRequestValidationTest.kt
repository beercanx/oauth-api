package uk.co.baconi.oauth.api.authorisation

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.http.*
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EmptySource
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationResponseType.Code
import uk.co.baconi.oauth.api.common.authorisation.CodeChallenge
import uk.co.baconi.oauth.api.common.authorisation.CodeChallengeMethod
import uk.co.baconi.oauth.api.common.client.ClientAction
import uk.co.baconi.oauth.api.common.client.ClientAction.Authorise
import uk.co.baconi.oauth.api.common.client.ClientAction.ProofKeyForCodeExchange
import uk.co.baconi.oauth.api.common.client.ClientConfiguration
import uk.co.baconi.oauth.api.common.client.ClientConfigurationRepository
import uk.co.baconi.oauth.api.common.client.ClientId
import uk.co.baconi.oauth.api.common.client.ClientType.Confidential
import uk.co.baconi.oauth.api.common.client.ClientType.Public
import uk.co.baconi.oauth.api.common.grant.GrantType.AuthorisationCode
import uk.co.baconi.oauth.api.common.scope.Scope
import uk.co.baconi.oauth.api.common.scope.ScopeRepository
import java.util.UUID


class AuthorisationRequestValidationTest {

    companion object {
        private const val REDIRECT_TOO_LONG = "https://flounder." +
                "mmacyeqoeybherifqiqlilvhhvojucozoqcjvoqsannwggj" +
                "zcoqzyeqgysqxfethhznfelovatopqwjlbscvmulngvgrkc" +
                "heqmphkhudtdpsobbkaeodyefgcgqmgwxgicoyuznrrikvr" +
                "wqhsvzlacmlzaxliiquyeorreqjvhxzhqdmgntduxuvhzsu" +
                "ljlkhvficyxivgphnzrqcfdgntxnksjjaefcpcmkufdbfyr" +
                "ypqwkiklfretrukpuncscqtfa.baconi.co.uk"
    }

    private val underTest = object : AuthorisationRequestValidation {

        override val scopeRepository = ScopeRepository()

        override val clientConfigurationRepository = mockk<ClientConfigurationRepository> {

            every { findByClientId("aardvark") } returns ClientConfiguration(
                id = ClientId("aardvark"),
                type = Confidential,
                redirectUris = setOf("https://aardvark.baconi.co.uk", "/aardvark"),
                allowedScopes = setOf(Scope("basic")),
                allowedActions = setOf(Authorise),
                allowedGrantTypes = setOf(AuthorisationCode)
            )

            every { findByClientId("badger") } returns null

            every { findByClientId("cicada") } returns ClientConfiguration(
                id = ClientId("cicada"),
                type = Confidential,
                redirectUris = setOf("https://cicada.baconi.co.uk"),
                allowedScopes = setOf(),
                allowedActions = setOf(),
                allowedGrantTypes = setOf()
            )

            every { findByClientId("dodo") } returns ClientConfiguration(
                id = ClientId("dodo"),
                type = Confidential,
                redirectUris = setOf("https://dodo.baconi.co.uk"),
                allowedScopes = setOf(),
                allowedActions = setOf(Authorise),
                allowedGrantTypes = setOf()
            )

            every { findByClientId("echidna") } returns ClientConfiguration(
                id = ClientId("echidna"),
                type = Public,
                redirectUris = setOf("https://echidna.baconi.co.uk"),
                allowedScopes = setOf(Scope("basic")),
                allowedActions = setOf(Authorise, ProofKeyForCodeExchange),
                allowedGrantTypes = setOf(AuthorisationCode)
            )

            every { findByClientId("flounder") } returns ClientConfiguration(
                id = ClientId("flounder"),
                type = Confidential,
                redirectUris = setOf(REDIRECT_TOO_LONG),
                allowedScopes = setOf(Scope("basic")),
                allowedActions = setOf(Authorise),
                allowedGrantTypes = setOf(AuthorisationCode)
            )
        }
    }

    @Test
    fun `return invalid client missing when the client configuration repository returns null`() {

        val result = underTest.validateAuthorisationRequest(Parameters.build {
            this["client_id"] = "badger"
        })

        assertSoftly(result) {
            shouldBeInstanceOf<AuthorisationRequest.InvalidClient>()
            reason shouldBe "missing"
        }
    }

    @Test
    fun `return invalid redirect missing when the redirect uri parameter is null`() {

        val result = underTest.validateAuthorisationRequest(Parameters.build {
            this["client_id"] = "aardvark"
        })

        assertSoftly(result) {
            shouldBeInstanceOf<AuthorisationRequest.InvalidRedirect>()
            reason shouldBe "missing"
        }
    }

    @Test
    fun `return redirect invalid when the redirect uri is not allowed for the client`() {

        val result = underTest.validateAuthorisationRequest(Parameters.build {
            this["client_id"] = "aardvark"
            this["redirect_uri"] = "https://badger.baconi.co.uk"
        })

        assertSoftly(result) {
            shouldBeInstanceOf<AuthorisationRequest.InvalidRedirect>()
            reason shouldBe "invalid"
        }
    }

    @Test
    fun `return invalid redirect when the redirect uri is not absolute`() {

        val result = underTest.validateAuthorisationRequest(Parameters.build {
            this["client_id"] = "aardvark"
            this["redirect_uri"] = "/aardvark"
        })

        assertSoftly(result) {
            shouldBeInstanceOf<AuthorisationRequest.InvalidRedirect>()
            reason shouldBe "invalid"
        }
    }

    @Test
    fun `return invalid when the redirect uri is too long`() {

        val result = underTest.validateAuthorisationRequest(Parameters.build {
            this["client_id"] = "flounder"
            this["redirect_uri"] = REDIRECT_TOO_LONG
        })

        assertSoftly(result) {
            shouldBeInstanceOf<AuthorisationRequest.Invalid>()
            redirectUri shouldBe REDIRECT_TOO_LONG
            state should beNull()

            error shouldBe "invalid_request"
            description shouldBe "invalid parameter: redirect_uri"
        }
    }

    @Test
    fun `return invalid when the client cannot authorise`() {

        val result = underTest.validateAuthorisationRequest(Parameters.build {
            this["client_id"] = "cicada"
            this["redirect_uri"] = "https://cicada.baconi.co.uk"
        })

        assertSoftly(result) {
            shouldBeInstanceOf<AuthorisationRequest.Invalid>()
            redirectUri shouldBe "https://cicada.baconi.co.uk"
            state should beNull()

            error shouldBe "unauthorized_client"
            description shouldBe "unauthorized client"
        }
    }

    @Test
    fun `return invalid when the client cannot be issued authorisation codes`() {

        val result = underTest.validateAuthorisationRequest(Parameters.build {
            this["client_id"] = "dodo"
            this["redirect_uri"] = "https://dodo.baconi.co.uk"
        })

        assertSoftly(result) {
            shouldBeInstanceOf<AuthorisationRequest.Invalid>()
            redirectUri shouldBe "https://dodo.baconi.co.uk"
            state should beNull()

            error shouldBe "unauthorized_client"
            description shouldBe "unauthorized client"
        }
    }

    @Test
    fun `return invalid when the response type is missing`() {

        val result = underTest.validateAuthorisationRequest(Parameters.build {
            this["client_id"] = "aardvark"
            this["redirect_uri"] = "https://aardvark.baconi.co.uk"
        })

        assertSoftly(result) {
            shouldBeInstanceOf<AuthorisationRequest.Invalid>()
            redirectUri shouldBe "https://aardvark.baconi.co.uk"
            state should beNull()

            error shouldBe "invalid_request"
            description shouldBe "missing parameter: response_type"
        }
    }

    @Test
    fun `return invalid when the response type is unsupported`() {

        val result = underTest.validateAuthorisationRequest(Parameters.build {
            this["client_id"] = "aardvark"
            this["redirect_uri"] = "https://aardvark.baconi.co.uk"
            this["response_type"] = "zip"
        })

        assertSoftly(result) {
            shouldBeInstanceOf<AuthorisationRequest.Invalid>()
            redirectUri shouldBe "https://aardvark.baconi.co.uk"
            state should beNull()

            error shouldBe "unsupported_response_type"
            description shouldBe "unsupported response type: zip"
        }
    }

    @Test
    fun `return invalid when the state is missing`() {

        val result = underTest.validateAuthorisationRequest(Parameters.build {
            this["client_id"] = "aardvark"
            this["redirect_uri"] = "https://aardvark.baconi.co.uk"
            this["response_type"] = "code"
        })

        assertSoftly(result) {
            shouldBeInstanceOf<AuthorisationRequest.Invalid>()
            redirectUri shouldBe "https://aardvark.baconi.co.uk"
            state should beNull()

            error shouldBe "invalid_request"
            description shouldBe "missing parameter: state"
        }
    }

    @Test
    fun `return invalid when the state is blank`() {

        val result = underTest.validateAuthorisationRequest(Parameters.build {
            this["client_id"] = "aardvark"
            this["redirect_uri"] = "https://aardvark.baconi.co.uk"
            this["response_type"] = "code"
            this["state"] = " "
        })

        assertSoftly(result) {
            shouldBeInstanceOf<AuthorisationRequest.Invalid>()
            redirectUri shouldBe "https://aardvark.baconi.co.uk"
            state shouldBe " "

            error shouldBe "invalid_request"
            description shouldBe "invalid parameter: state"
        }
    }

    @Test
    fun `return invalid when the state is too long`() {

        val state = "mzovcgpgvtzehpgxiweowtegabrfqkhsunjqnidennznkxfkbhnqzwood" +
                "tfucnssdaosmaewpiqmvlgofckyojlwkwyzjgxotewtxtmdnnrmchvhjkzafh" +
                "vxvjrsmzgohqhsuraagphxwncualobfcdbnzocpdlrzaicmegysyhspvzewqz" +
                "pnjnsfsaoaapwgofhslaroshzmvrcxnicofldfqnbsguucaylvcblwabvzhap" +
                "edwnukgzlktlqloffpwx"

        val result = underTest.validateAuthorisationRequest(Parameters.build {
            this["client_id"] = "aardvark"
            this["redirect_uri"] = "https://aardvark.baconi.co.uk"
            this["response_type"] = "code"
            this["state"] = state
        })

        assertSoftly(result) {
            shouldBeInstanceOf<AuthorisationRequest.Invalid>()
            redirectUri shouldBe "https://aardvark.baconi.co.uk"
            state shouldBe state

            error shouldBe "invalid_request"
            description shouldBe "invalid parameter: state"
        }
    }

    @Test
    fun `return invalid when the client cannot be issued a scope`() {

        val result = underTest.validateAuthorisationRequest(Parameters.build {
            this["client_id"] = "aardvark"
            this["redirect_uri"] = "https://aardvark.baconi.co.uk"
            this["response_type"] = "code"
            this["state"] = "000cdd20-f577-4e9a-b49c-7ded0b06188d"
            this["scope"] = "basic profile::read"
        })

        assertSoftly(result) {
            shouldBeInstanceOf<AuthorisationRequest.Invalid>()
            redirectUri shouldBe "https://aardvark.baconi.co.uk"
            state shouldBe "000cdd20-f577-4e9a-b49c-7ded0b06188d"

            error shouldBe "invalid_request"
            description shouldBe "invalid parameter: scope"
        }
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @CsvSource("000cdd20-f577-4e9a-b49c-7ded0b06188d")
    fun `return invalid when the user has aborted the authentication attempt`(state: String?) {

        val result = underTest.validateAuthorisationRequest(Parameters.build {
            this["client_id"] = "aardvark"
            this["redirect_uri"] = "https://aardvark.baconi.co.uk"
            if(state is String) this["state"] = state
            this["abort"] = "true"
        })

        assertSoftly(result) {
            shouldBeInstanceOf<AuthorisationRequest.Invalid>()
            redirectUri shouldBe "https://aardvark.baconi.co.uk"
            state shouldBe state

            error shouldBe "access_denied"
            description shouldBe "user aborted"
        }
    }

    @Test
    fun `return invalid when the code challenge is missing`() {

        val randomState = UUID.randomUUID().toString()

        val result = underTest.validateAuthorisationRequest(Parameters.build {
            this["client_id"] = "echidna"
            this["redirect_uri"] = "https://echidna.baconi.co.uk"
            this["response_type"] = "code"
            this["state"] = randomState

            this["code_challenge_method"] = "S256"
        })

        assertSoftly(result) {
            shouldBeInstanceOf<AuthorisationRequest.Invalid>()
            redirectUri shouldBe "https://echidna.baconi.co.uk"
            state shouldBe randomState

            error shouldBe "invalid_request"
            description shouldBe "missing parameter: code_challenge"
        }
    }

    @Test
    fun `return invalid when the code challenge is blank`() {

        val randomState = UUID.randomUUID().toString()

        val result = underTest.validateAuthorisationRequest(Parameters.build {
            this["client_id"] = "echidna"
            this["redirect_uri"] = "https://echidna.baconi.co.uk"
            this["response_type"] = "code"
            this["state"] = randomState
            this["code_challenge"] = " "
            this["code_challenge_method"] = "S256"
        })

        assertSoftly(result) {
            shouldBeInstanceOf<AuthorisationRequest.Invalid>()
            redirectUri shouldBe "https://echidna.baconi.co.uk"
            state shouldBe randomState

            error shouldBe "invalid_request"
            description shouldBe "invalid parameter: code_challenge"
        }
    }

    @Test
    fun `return invalid when the code challenge is too long`() {

        val randomState = UUID.randomUUID().toString()

        val result = underTest.validateAuthorisationRequest(Parameters.build {
            this["client_id"] = "echidna"
            this["redirect_uri"] = "https://echidna.baconi.co.uk"
            this["response_type"] = "code"
            this["state"] = randomState
            this["code_challenge"] = "baxfdtzqnkmmnmdpdcdbcqlctymrxsiyzlcpyqq" +
                    "dgwukoszhruwoueogamqrfurzjvffiolzhyrdzjjjmlgfxlvbjptirif" +
                    "sjtasdrklzntmgiuyjrtszwlmcfqvoqyuwedzdqdcfckfixcjjnebbsd" +
                    "sjeqvjczanrnbeebfczughxavowwnezyuxjfvvcoirabndjmfaelolgn" +
                    "sozagckcsxwiqoaqjpzhjzdjxsvdxxzzqcnfpawjhmlqmlckhnmep"
            this["code_challenge_method"] = "S256"
        })

        assertSoftly(result) {
            shouldBeInstanceOf<AuthorisationRequest.Invalid>()
            redirectUri shouldBe "https://echidna.baconi.co.uk"
            state shouldBe randomState

            error shouldBe "invalid_request"
            description shouldBe "invalid parameter: code_challenge"
        }
    }

    @Test
    fun `return invalid when the code challenge method is missing`() {

        val randomState = UUID.randomUUID().toString()

        val result = underTest.validateAuthorisationRequest(Parameters.build {
            this["client_id"] = "echidna"
            this["redirect_uri"] = "https://echidna.baconi.co.uk"
            this["response_type"] = "code"
            this["state"] = randomState
            this["code_challenge"] = "baxfdtzqnkmmnmdpdcdbcqlctymrxsiyzlcpyqq"
        })

        assertSoftly(result) {
            shouldBeInstanceOf<AuthorisationRequest.Invalid>()
            redirectUri shouldBe "https://echidna.baconi.co.uk"
            state shouldBe randomState

            error shouldBe "invalid_request"
            description shouldBe "missing parameter: code_challenge_method"
        }
    }

    @Test
    fun `return invalid when the code challenge method is invalid`() {

        val randomState = UUID.randomUUID().toString()

        val result = underTest.validateAuthorisationRequest(Parameters.build {
            this["client_id"] = "echidna"
            this["redirect_uri"] = "https://echidna.baconi.co.uk"
            this["response_type"] = "code"
            this["state"] = randomState
            this["code_challenge"] = "baxfdtzqnkmmnmdpdcdbcqlctymrxsiyzlcpyqq"
            this["code_challenge_method"] = "plain"
        })

        assertSoftly(result) {
            shouldBeInstanceOf<AuthorisationRequest.Invalid>()
            redirectUri shouldBe "https://echidna.baconi.co.uk"
            state shouldBe randomState

            error shouldBe "invalid_request"
            description shouldBe "invalid parameter: code_challenge_method"
        }
    }

    @Test
    fun `return valid PKCE when the parameters are all valid`() {

        val result = underTest.validateAuthorisationRequest(Parameters.build {
            this["client_id"] = "echidna"
            this["redirect_uri"] = "https://echidna.baconi.co.uk"
            this["response_type"] = "code"
            this["state"] = "13385e28-d0e5-41e1-9e0c-8c9df3812279"
            this["scope"] = "basic"
            this["code_challenge"] = "u7a9y9LFsPSeFohPRcPPGxt8oGYaJE16qHH7H7eM9K0"
            this["code_challenge_method"] = "S256"
        })

        assertSoftly(result) {
            shouldBeInstanceOf<AuthorisationRequest.PKCE>()
            redirectUri shouldBe "https://echidna.baconi.co.uk"
            state shouldBe "13385e28-d0e5-41e1-9e0c-8c9df3812279"

            responseType shouldBe Code
            clientId shouldBe ClientId("echidna")
            scopes shouldHaveSingleElement Scope("basic")

            codeChallenge shouldBe CodeChallenge("u7a9y9LFsPSeFohPRcPPGxt8oGYaJE16qHH7H7eM9K0")
            codeChallengeMethod shouldBe CodeChallengeMethod.S256
        }
    }

    @Test
    fun `return valid Basic when the parameters are all valid`() {

        val result = underTest.validateAuthorisationRequest(Parameters.build {
            this["client_id"] = "aardvark"
            this["redirect_uri"] = "https://aardvark.baconi.co.uk"
            this["response_type"] = "code"
            this["state"] = "000cdd20-f577-4e9a-b49c-7ded0b06188d"
            this["scope"] = "basic"
        })

        assertSoftly(result) {
            shouldBeInstanceOf<AuthorisationRequest.Basic>()
            redirectUri shouldBe "https://aardvark.baconi.co.uk"
            state shouldBe "000cdd20-f577-4e9a-b49c-7ded0b06188d"

            responseType shouldBe Code
            clientId shouldBe ClientId("aardvark")
            scopes shouldHaveSingleElement Scope("basic")
        }
    }
}