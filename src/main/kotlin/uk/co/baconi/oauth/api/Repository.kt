package uk.co.baconi.oauth.api

interface Repository<TYPE, ID> {

    /**
     * Insert a new [TYPE] record
     */
    fun insert(new: TYPE)

    /**
     * Delete an existing record based on the primary id.
     */
    fun delete(id: ID)

    /**
     * Find an existing record based on the primary id.
     */
    fun findById(id: ID): TYPE?

}