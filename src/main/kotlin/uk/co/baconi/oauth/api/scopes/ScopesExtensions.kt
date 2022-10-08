package uk.co.baconi.oauth.api.scopes

import uk.co.baconi.oauth.api.client.ClientConfiguration
import uk.co.baconi.oauth.api.client.ClientPrincipal
import uk.co.baconi.oauth.api.client.ClientType.Confidential
import uk.co.baconi.oauth.api.client.ClientType.Public
import uk.co.baconi.oauth.api.client.ConfidentialClient
import uk.co.baconi.oauth.api.client.PublicClient
import uk.co.baconi.oauth.api.enums.deserialise

// TODO - Depending on validation message requirements, this might collapse down to just a Set<Scopes>?
fun String?.parseAsScopes(configuration: ClientConfiguration?): Triple<Boolean, Boolean, Set<Scopes>> {
    return when (configuration?.type) {
        null -> Triple(first = true, second = true, third = emptySet())
        Confidential -> parseAsScopes(ConfidentialClient(configuration))
        Public -> parseAsScopes(PublicClient(configuration))
    }
}

// TODO - Depending on validation message requirements, this might collapse down to just a Set<Scopes>?
fun String?.parseAsScopes(principal: ClientPrincipal): Triple<Boolean, Boolean, Set<Scopes>> {
    val raw = this?.rawScopes()
    val parsed = raw?.parseScopes()
    val valid = parsed?.validateScopes(principal)
    return Triple(raw?.size == parsed?.size, parsed?.size == valid?.size, valid ?: emptySet())
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
