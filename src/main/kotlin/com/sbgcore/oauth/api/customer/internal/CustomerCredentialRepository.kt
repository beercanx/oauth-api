package com.sbgcore.oauth.api.customer.internal

import com.sbgcore.oauth.api.Repository

interface CustomerCredentialRepository : Repository<CustomerCredential, String> {

    /**
     * Find a [CustomerCredential] by its username.
     */
    fun findByUsername(username: String): CustomerCredential? = findById(username)

}