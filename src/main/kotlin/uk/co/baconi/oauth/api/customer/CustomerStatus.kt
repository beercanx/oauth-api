package uk.co.baconi.oauth.api.customer

import org.dizitart.no2.objects.Id

data class CustomerStatus(
    @Id val username: String,
    val state: CustomerState,
    val isLocked: Boolean = false,
    val changePassword: Boolean = false
)
