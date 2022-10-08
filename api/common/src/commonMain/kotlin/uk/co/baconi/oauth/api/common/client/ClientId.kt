package uk.co.baconi.oauth.api.common.client

import kotlinx.serialization.Serializable

@Serializable(with = ClientIdSerializer::class)
data class ClientId(val value: String)
