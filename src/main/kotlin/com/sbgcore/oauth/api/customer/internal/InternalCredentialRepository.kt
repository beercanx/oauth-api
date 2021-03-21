package com.sbgcore.oauth.api.customer.internal

import com.sbgcore.oauth.api.Repository

interface InternalCredentialRepository : Repository<InternalCredential, String> {

    /**
     * Find a [InternalCredential] by its username.
     */
    fun findByUsername(username: String): InternalCredential? = findById(username)

}