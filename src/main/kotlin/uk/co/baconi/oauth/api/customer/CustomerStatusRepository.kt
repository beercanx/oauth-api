package uk.co.baconi.oauth.api.customer

import uk.co.baconi.oauth.api.Repository

interface CustomerStatusRepository : Repository<CustomerStatus, String> {

    /**
     * Find a [CustomerStatus] by its username.
     */
    fun findByUsername(username: String): CustomerStatus? = findById(username)

}