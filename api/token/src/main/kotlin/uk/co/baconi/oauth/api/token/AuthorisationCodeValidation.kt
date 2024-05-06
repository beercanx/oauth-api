package uk.co.baconi.oauth.api.token

import io.ktor.http.*
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCode
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeRepository
import uk.co.baconi.oauth.api.common.authorisation.CodeChallengeMethod.S256
import uk.co.baconi.oauth.api.common.client.ClientAction.ProofKeyForCodeExchange
import uk.co.baconi.oauth.api.common.client.ClientPrincipal
import uk.co.baconi.oauth.api.common.client.PublicClient
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

    fun validateAuthorisationCode(
        client: ClientPrincipal,
        code: UUID,
        redirectUri: String,
        codeVerifier: String? = null
    ): AuthorisationCode? {

        val authorisationCode = authorisationCodeRepository.findById(code)

        // TODO - Change return type to support providing a reason, to help with unit testing and trace logging.

        return when {

            // Validate the [code] is a valid code via a repository
            authorisationCode == null -> null // TODO - This should trigger session destruction?

            // Validate the [client_id] is the same as what was used to generate the [code]
            authorisationCode.clientId != client.id -> null

            // Validate the [redirect_uri] is the same as what was used to generate the [code]
            authorisationCode.redirectUri != redirectUri -> null

            // Verify the authorisation code has not yet expired.
            authorisationCode.hasExpired() -> null

            // Verify the authorisation code has not been used. https://www.rfc-editor.org/rfc/rfc6749#section-10.5
            authorisationCode.used -> null // TODO - SHOULD attempt to revoke all access tokens already granted based on the compromised authorization code.

            // Verify if the client MUST be exchanging PKCE
            client is PublicClient && authorisationCode !is AuthorisationCode.PKCE -> null

            // Verify the code verifier and then return
            authorisationCode is AuthorisationCode.PKCE -> when {

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
     * See https://www.rfc-editor.org/rfc/rfc7636#section-4.6
     */
    fun validateCodeVerifier(authorisationCode: AuthorisationCode.PKCE, codeVerifier: String): Boolean {

        fun ascii(string: String): ByteArray = string.toByteArray(Charsets.US_ASCII)
        fun sha256(bytes: ByteArray): ByteArray = MessageDigest.getInstance("SHA-256").digest(bytes)
        fun base64UrlEncode(bytes: ByteArray): String = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)

        return when (authorisationCode.codeChallengeMethod) {
            S256 -> base64UrlEncode(sha256(ascii(codeVerifier))) == authorisationCode.codeChallenge.value
        }
    }
}
