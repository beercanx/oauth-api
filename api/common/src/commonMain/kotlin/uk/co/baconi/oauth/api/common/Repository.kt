package uk.co.baconi.oauth.api.common

interface Repository<TYPE, ID> {

    /**
     * Find an existing record based on the primary id.
     */
    fun findById(id: ID): TYPE?

    /**
     * Provides an optional update component.
     */
    interface WithInsert<TYPE> {

        /**
         * Insert a new [TYPE] record.
         */
        fun insert(new: TYPE)
    }

    /**
     * Provides an optional update component.
     */
    interface WithDelete<TYPE, ID> {

        /**
         * Delete an existing record based on the primary id.
         */
        fun delete(id: ID)

        /**
         * Delete an existing [TYPE] record.
         */
        fun delete(record: TYPE)
    }

    /**
     * Provides an optional update component.
     */
    interface WithUpdate<TYPE> {

        /**
         * Update an existing [TYPE] record.
         */
        fun update(record: TYPE)
    }
}