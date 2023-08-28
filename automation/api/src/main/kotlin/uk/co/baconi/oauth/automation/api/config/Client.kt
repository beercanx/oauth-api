package uk.co.baconi.oauth.automation.api.config

import java.net.URI

sealed interface Client {
    val id: ClientId
    val type: ClientType
    val redirectUri: URI
    val grantTypes: Set<GrantType>
    // TODO - Consider some thing like actionTypes for "can introspect" look ups
}

interface PublicClient : Client {
    override val type: ClientType get() = ClientType.Public
}

interface ConfidentialClient : Client {
    override val type: ClientType get() = ClientType.Confidential
    val secret: ClientSecret
}
