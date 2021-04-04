package com.sbgcore.oauth.api.customer.internal

import org.dizitart.no2.objects.Id

data class CustomerStatus(
    @Id val username: String,
    val state: CustomerState,
    val isLocked: Boolean = false,
    val changePassword: Boolean = false
)
