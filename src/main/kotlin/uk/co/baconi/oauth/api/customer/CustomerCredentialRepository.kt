package uk.co.baconi.oauth.api.customer

import uk.co.baconi.oauth.api.Repository

interface CustomerCredentialRepository : Repository<CustomerCredential, String> {

    /**
     * Find a [CustomerCredential] by its username.
     */
    fun findByUsername(username: String): CustomerCredential? = findById(username)

}