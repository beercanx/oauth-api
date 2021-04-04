package com.sbgcore.oauth.api.customer.internal

import org.dizitart.no2.objects.Id

data class CustomerCredential(
    @Id val username: String,
    val secret: String,
    val temporary: Boolean = false,
    val locked: Boolean = false
) {
    override fun toString(): String {
        return "CustomerCredential(username='$username', temporary=$temporary, locked=$locked)"
    }
}
