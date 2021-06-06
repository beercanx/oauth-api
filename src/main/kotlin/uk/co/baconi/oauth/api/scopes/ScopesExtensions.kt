package uk.co.baconi.oauth.api.scopes

import uk.co.baconi.oauth.api.client.ClientConfiguration
import uk.co.baconi.oauth.api.client.ClientPrincipal
import uk.co.baconi.oauth.api.client.ClientType.Confidential
import uk.co.baconi.oauth.api.client.ClientType.Public
import uk.co.baconi.oauth.api.client.ConfidentialClient
import uk.co.baconi.oauth.api.client.PublicClient
import uk.co.baconi.oauth.api.enums.deserialise

fun String?.parseAsScopes(configuration: ClientConfiguration?): Triple<List<String>?, List<Scopes>?, Set<Scopes>> {
    return when (configuration?.type) {
        null -> Triple(null, null, emptySet())
        Confidential -> parseAsScopes(ConfidentialClient(configuration))
        Public -> parseAsScopes(PublicClient(configuration))
    }
}

fun String?.parseAsScopes(principal: ClientPrincipal): Triple<List<String>?, List<Scopes>?, Set<Scopes>> {
    val rawScopes = this?.rawScopes()
    val parsedScopes = rawScopes?.parseScopes()
    val validScopes = parsedScopes?.validateScopes(principal) ?: emptySet()
    return Triple(rawScopes, parsedScopes, validScopes)
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
