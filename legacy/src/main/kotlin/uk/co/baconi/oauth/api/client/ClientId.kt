package uk.co.baconi.oauth.api.client

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ClientId {

    @SerialName("consumer-x") ConsumerX,
    @SerialName("consumer-y") ConsumerY,
    @SerialName("consumer-z") ConsumerZ,

    ;

}