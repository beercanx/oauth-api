package uk.co.baconi.oauth.api.scopes

import uk.co.baconi.oauth.api.client.ClientConfiguration
import uk.co.baconi.oauth.api.client.ClientPrincipal
import uk.co.baconi.oauth.api.client.ClientType.Confidential
import uk.co.baconi.oauth.api.client.ClientType.Public
import uk.co.baconi.oauth.api.client.ConfidentialClient
import uk.co.baconi.oauth.api.client.PublicClient
import uk.co.baconi.oauth.api.enums.deserialise

/**
 * Parses [String] as [Scopes] for a given [ClientPrincipal].
 *
 * @returns null when provided with a null [ClientConfiguration] or
 *          defined with scopes that either don't exist or are invalid for the principal.
 */
fun String?.parseAsScopes(configuration: ClientConfiguration?): Set<Scopes>? {
    return when (configuration?.type) {
        null -> null
        Confidential -> parseAsScopes(ConfidentialClient(configuration))
        Public -> parseAsScopes(PublicClient(configuration))
    }
}

/**
 * Parses [String] as [Scopes] for a given [ClientPrincipal].
 *
 * @returns null when defined with scopes that either don't exist or are invalid for the principal.
 */
fun String?.parseAsScopes(principal: ClientPrincipal): Set<Scopes>? {

    // Enable the parameter to be optional, so we return the clients full allowed set.
    if (this.isNullOrBlank()) {
        return principal.configuration.allowedScopes
    }

    val raw = this.rawScopes()
    val parsed = raw.parseScopes()
    val valid = parsed.validateScopes(principal)

    // unknown scopes || invalid scopes
    return if (raw.size != parsed.size || parsed.size != valid.size) {
        null
    } else {
        valid
    }
}

private fun String.rawScopes(): List<String> {
    return split(" ").filter(String::isNotBlank)
}

private fun List<String>.parseScopes(): List<Scopes> {
    return mapNotNull { s -> s.deserialise<Scopes>() }
}

private fun List<Scopes>.validateScopes(principal: ClientPrincipal): Set<Scopes> {
    return filter(principal::canBeIssued).toSet()
}
