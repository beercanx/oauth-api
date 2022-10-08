package uk.co.baconi.oauth.common.authentication

data class CustomerStatus(
    val username: String,
    val state: CustomerState,
    // TODO - Include created_at
    // TODO - Include modified_at
)
