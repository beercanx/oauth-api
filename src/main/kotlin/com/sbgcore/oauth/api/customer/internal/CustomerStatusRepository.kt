package com.sbgcore.oauth.api.customer.internal

import com.sbgcore.oauth.api.Repository

interface CustomerStatusRepository : Repository<CustomerStatus, String> {

    /**
     * Find a [CustomerStatus] by its username.
     */
    fun findByUsername(username: String): CustomerStatus? = findById(username)

}