package uk.co.baconi.oauth.api.authorisation

import io.ktor.http.*
import io.ktor.server.application.*
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeTable.CODE_CHALLENGE_LENGTH
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeTable.REDIRECT_URI_LENGTH
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeTable.STATE_LENGTH
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationResponseType
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationResponseType.Code
import uk.co.baconi.oauth.api.common.authorisation.CodeChallenge
import uk.co.baconi.oauth.api.common.authorisation.CodeChallengeMethod
import uk.co.baconi.oauth.api.common.client.ClientAction.Authorise
import uk.co.baconi.oauth.api.common.client.ClientAction.ProofKeyForCodeExchange
import uk.co.baconi.oauth.api.common.client.ClientConfigurationRepository
import uk.co.baconi.oauth.api.common.client.ClientPrincipal
import uk.co.baconi.oauth.api.common.grant.GrantType.AuthorisationCode
import uk.co.baconi.oauth.api.common.scope.ScopeRepository
import uk.co.baconi.oauth.api.common.scope.ScopesDeserializer

private const val CLIENT_ID = "client_id"
private const val REDIRECT_URI = "redirect_uri"
private const val RESPONSE_TYPE = "response_type"
private const val SCOPE = "scope"
private const val STATE = "state"
private const val CODE_CHALLENGE = "code_challenge"
private const val CODE_CHALLENGE_METHOD = "code_challenge_method"
private const val ABORT = "abort"

interface AuthorisationRequestValidation {

    val scopeRepository: ScopeRepository
    val clientConfigurationRepository: ClientConfigurationRepository

    suspend fun ApplicationCall.validateAuthorisationRequest(): AuthorisationRequest {
        return validateAuthorisationRequest(request.queryParameters)
    }

    fun validateAuthorisationRequest(params: Parameters): AuthorisationRequest {

        val config = params[CLIENT_ID]?.let(clientConfigurationRepository::findByClientId)
        val principal = config?.let(ClientPrincipal::fromConfiguration)

        val redirectUri = params[REDIRECT_URI]
        val responseType = params[RESPONSE_TYPE]?.let(AuthorisationResponseType::fromValueOrNull)
        val rawScopes = params[SCOPE]?.let(ScopesDeserializer::deserialize) ?: emptySet()
        val scopes = rawScopes.mapNotNull(scopeRepository::findById).toSet()
        val state = params[STATE]
        val codeChallenge = params[CODE_CHALLENGE]
        val codeChallengeMethod = params[CODE_CHALLENGE_METHOD]?.let(CodeChallengeMethod::fromNameOrNull)
        val abort = params[ABORT]?.toBooleanStrictOrNull() ?: false

        return when {

            config == null -> AuthorisationRequest.InvalidClient("missing")
            principal == null -> AuthorisationRequest.InvalidClient("missing")

            redirectUri == null -> AuthorisationRequest.InvalidRedirect("missing")
            !principal.hasRedirectUri(redirectUri) -> AuthorisationRequest.InvalidRedirect("invalid")
            !redirectUri.isAbsoluteURI(false) -> AuthorisationRequest.InvalidRedirect("invalid")
            redirectUri.length >= REDIRECT_URI_LENGTH -> invalidParameter(redirectUri, state, REDIRECT_URI)

            // Support aborting an authorisation via a users choice
            abort -> AuthorisationRequest.Invalid(
                redirectUri = redirectUri,
                error = "access_denied",
                description = "user aborted",
                state = state
            )

            // Currently only support authorisation code
            !(principal.can(Authorise) && principal.can(AuthorisationCode)) -> AuthorisationRequest.Invalid(
                redirectUri = redirectUri,
                error = "unauthorized_client",
                description = "unauthorized client",
                state = state
            )

            params[RESPONSE_TYPE] == null -> missingParameter(redirectUri, state, RESPONSE_TYPE)
            responseType == null || responseType != Code -> AuthorisationRequest.Invalid(
                redirectUri = redirectUri,
                error = "unsupported_response_type",
                description = "unsupported response type: ${params[RESPONSE_TYPE]}",
                state = state
            )

            // Enforce the use of a state parameter
            state == null -> missingParameter(redirectUri, null, STATE)
            state.isBlank() -> invalidParameter(redirectUri, state, STATE)
            state.length >= STATE_LENGTH -> invalidParameter(redirectUri, state, STATE)

            // The requested scope is invalid, unknown, or malformed.
            rawScopes.size != scopes.size -> invalidParameter(redirectUri, state, SCOPE)
            !scopes.all(principal::canBeIssued) -> invalidParameter(redirectUri, state, SCOPE)

            // If a client "can" do PKCE, we're going to enforce that they do.
            principal.can(ProofKeyForCodeExchange) -> when {

                codeChallenge == null -> missingParameter(redirectUri, state, CODE_CHALLENGE)
                codeChallenge.isBlank() -> invalidParameter(redirectUri, state, CODE_CHALLENGE)
                codeChallenge.length >= CODE_CHALLENGE_LENGTH -> invalidParameter(redirectUri, state, CODE_CHALLENGE)

                params[CODE_CHALLENGE_METHOD] == null -> missingParameter(redirectUri, state, CODE_CHALLENGE_METHOD)
                codeChallengeMethod == null -> invalidParameter(redirectUri, state, CODE_CHALLENGE_METHOD)

                else -> AuthorisationRequest.PKCE(
                    base = AuthorisationRequest.Basic(
                        responseType = responseType,
                        clientId = config.id,
                        redirectUri = redirectUri,
                        state = state,
                        scopes = scopes
                    ),
                    codeChallenge = codeChallenge.let(::CodeChallenge),
                    codeChallengeMethod = codeChallengeMethod
                )
            }

            else -> AuthorisationRequest.Basic(
                responseType = responseType,
                clientId = config.id,
                redirectUri = redirectUri,
                state = state,
                scopes = scopes
            )
        }
    }

    private fun missingParameter(redirectUri: String, state: String?, name: String) = invalid(
        redirectUri = redirectUri,
        state = state,
        name = name,
        type = "missing"
    )

    private fun invalidParameter(redirectUri: String, state: String?, name: String) = invalid(
        redirectUri = redirectUri,
        state = state,
        name = name,
        type = "invalid"
    )

    private fun invalid(redirectUri: String, state: String?, name: String, type: String) = AuthorisationRequest.Invalid(
        redirectUri = redirectUri,
        error = "invalid_request",
        description = "$type parameter: $name",
        state = state
    )
}
