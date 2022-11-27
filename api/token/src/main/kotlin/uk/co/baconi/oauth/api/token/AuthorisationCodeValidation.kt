package uk.co.baconi.oauth.api.token

import io.ktor.http.*
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCode
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeRepository
import uk.co.baconi.oauth.api.common.authorisation.CodeChallengeMethod.S256
import uk.co.baconi.oauth.api.common.client.ClientAction.ProofKeyForCodeExchange
import uk.co.baconi.oauth.api.common.client.ClientPrincipal
import uk.co.baconi.oauth.api.common.uuid.UUIDSerializer
import uk.co.baconi.oauth.api.token.TokenErrorType.InvalidGrant
import uk.co.baconi.oauth.api.token.TokenErrorType.InvalidRequest
import uk.co.baconi.oauth.api.token.TokenRequest.Invalid
import java.security.MessageDigest
import java.util.*

private const val CODE = "code"
private const val REDIRECT_URI = "redirect_uri"
private const val CODE_VERIFIER = "code_verifier"

interface AuthorisationCodeValidation {

    val authorisationCodeRepository: AuthorisationCodeRepository

    fun validateAuthorisationCodeRequest(parameters: Parameters, client: ClientPrincipal): TokenRequest {

        val redirectUri = parameters[REDIRECT_URI]
        val uuid = parameters[CODE]?.let(UUIDSerializer::fromValueOrNull)
        val codeVerifier = parameters[CODE_VERIFIER]
        val mustUsePkce = client.can(ProofKeyForCodeExchange)

        return when {
            redirectUri == null -> Invalid(InvalidRequest, "missing parameter: $REDIRECT_URI")
            redirectUri.isBlank() -> Invalid(InvalidRequest, "invalid parameter: $REDIRECT_URI")
            !client.hasRedirectUri(redirectUri) -> Invalid(InvalidRequest, "invalid parameter: $REDIRECT_URI")

            parameters[CODE] == null -> Invalid(InvalidRequest, "missing parameter: $CODE")
            uuid == null -> Invalid(InvalidGrant, "invalid parameter: $CODE")

            mustUsePkce && codeVerifier == null -> Invalid(InvalidRequest, "missing parameter: $CODE_VERIFIER")
            mustUsePkce && codeVerifier.isNullOrBlank() -> Invalid(InvalidRequest, "invalid parameter: $CODE_VERIFIER")

            else -> when (val code = validateAuthorisationCode(client, uuid, redirectUri, codeVerifier)) {
                // TODO - Verify this is the most helpful we can be without compromising security
                null -> Invalid(InvalidGrant, "invalid parameter: $CODE")
                else -> AuthorisationCodeRequest(client, code)
            }
        }
    }

    // TODO - Add trace logging to assist in debugging consumer integrations?
    fun validateAuthorisationCode(
        client: ClientPrincipal,
        code: UUID,
        redirectUri: String,
        codeVerifier: String? = null
    ): AuthorisationCode? {

        val authorisationCode = authorisationCodeRepository.findById(code)

        return when {

            // Validate the [code] is a valid code via a repository
            authorisationCode == null -> null

            // Validate the [client_id] is the same as what was used to generate the [code]
            authorisationCode.clientId != client.id -> null

            // Validate the [redirect_uri] is the same as what was used to generate the [code]
            authorisationCode.redirectUri != redirectUri -> null

            // Verify the authorisation code has not yet expired.
            authorisationCode.hasExpired() -> null

            // Verify the code verifier and then return
            authorisationCode is AuthorisationCode.Pkce -> when {

                // Verify there is a code verifier provided
                codeVerifier == null -> null

                // Verify the actual code via the code verifier process
                !validateCodeVerifier(authorisationCode, codeVerifier) -> null

                // Looks valid to me 👍
                else -> authorisationCode
            }

            // Verify that there is no code verifier
            codeVerifier != null -> null

            // Looks valid to me 👍
            else -> authorisationCode
        }
    }

    /**
     * See https://datatracker.ietf.org/doc/html/rfc7636#section-4.6
     */
    fun validateCodeVerifier(authorisationCode: AuthorisationCode.Pkce, codeVerifier: String): Boolean {

        fun ascii(string: String): ByteArray = string.toByteArray(Charsets.US_ASCII)
        fun sha256(bytes: ByteArray): ByteArray = MessageDigest.getInstance("SHA-256").digest(bytes)
        fun base64UrlEncode(bytes: ByteArray): String = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)

        return when (authorisationCode.codeChallengeMethod) {
            S256 -> base64UrlEncode(sha256(ascii(codeVerifier))) == authorisationCode.codeChallenge.value
        }
    }
}