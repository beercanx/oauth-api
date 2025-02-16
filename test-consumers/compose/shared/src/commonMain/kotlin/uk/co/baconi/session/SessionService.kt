package uk.co.baconi.session

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.ContentType.Application.FormUrlEncoded
import io.ktor.http.ContentType.Application.Json
import io.ktor.util.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import uk.co.baconi.createHttpClient
import uk.co.baconi.getLocalhost
import uk.co.baconi.session.oauth.AuthorisationCode
import uk.co.baconi.session.oauth.CodeChallenge
import uk.co.baconi.session.oauth.CodeVerifier
import uk.co.baconi.session.oauth.State
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.random.Random

class SessionService(
    private val httpClient: HttpClient = createHttpClient()
) {

    private val scopes = listOf("basic")
    private val clientId = "test-consumer-compose"
    private val redirectUri = getRedirectUri() // TODO - Replace with external properties

    private val authorisationEndpoint = "http://${getLocalhost()}:8080/authorise" // TODO - Replace with external properties
    private val tokenEndpoint = "http://${getLocalhost()}:8080/token" // TODO - Replace with external properties

    private val codeVerifierLength = 64
    private val codeVerifierCharPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    fun createState(): State {
        return State(generateUUID())
    }

    fun createVerifier(): CodeVerifier {
        return (1..codeVerifierLength)
            .map { Random.nextInt(0, codeVerifierCharPool.size).let { codeVerifierCharPool[it] } }
            .joinToString("")
            .let(::CodeVerifier)
    }

    @OptIn(ExperimentalEncodingApi::class, InternalAPI::class)
    suspend fun createChallenge(verifier: CodeVerifier): CodeChallenge {

        // Should be ASCII, but it's not available, so we're hoping limiting the verifier to ASCII characters helps.
        fun ascii(string: String): ByteArray = string.toByteArray()
        suspend fun sha256(bytes: ByteArray): ByteArray = Digest("SHA-256").build(bytes)
        fun base64UrlEncodeWithoutPadding(bytes: ByteArray): String = Base64.UrlSafe.encode(bytes).split('=')[0]

        // https://www.rfc-editor.org/rfc/rfc7636#section-4.6
        return base64UrlEncodeWithoutPadding(sha256(ascii(verifier.value))).let(::CodeChallenge)
    }

    fun authoriseUrl(state: State, codeChallenge: CodeChallenge): Url {
        return URLBuilder().apply {
                takeFrom(authorisationEndpoint)
                parameters["response_type"] = "code"
                parameters["client_id"] = clientId
                parameters["redirect_uri"] = redirectUri.toString()
                parameters["state"] = state.value
                parameters["scope"] = scopes.joinToString(separator = " ")
                parameters["code_challenge"] = codeChallenge.value
                parameters["code_challenge_method"] = "S256"
            }.build()
    }

    suspend fun authorisationCodeGrant(code: AuthorisationCode, codeVerifier: CodeVerifier): Session {

        val response = httpClient.post(tokenEndpoint) {
            accept(Json)
            contentType(FormUrlEncoded)
            setBody(FormDataContent(parameters {
                append("grant_type", "authorization_code")
                append("client_id", clientId)
                append("redirect_uri", redirectUri.toString())
                append("code", code.value)
                append("code_verifier", codeVerifier.value)
            }))
        }

        return response.body<Session>()
    }

}