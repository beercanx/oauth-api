package com.sbgcore.oauth.api.customer

import com.sbgcore.oauth.api.customer.CustomerState.Active
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.getRepository
import org.dizitart.kno2.nitrite
import org.dizitart.no2.Nitrite
import java.io.Closeable

class NitriteCustomerStatusRepository(database: Nitrite) : CustomerStatusRepository, Closeable by database {

    /**
     * Create a new instance of [NitriteCustomerStatusRepository] with an in-memory instance of [Nitrite]
     */
    constructor() : this(
        nitrite(userId = "customer-status", password = ":_,z;&b7&]u{E'!&fz<PL35NvqTKx'5")
    )

    private val repository = database.getRepository<CustomerStatus> {

        // Add some initial test users
        insert(CustomerStatus(username = "AARDVARK", state = Active, isLocked = false, changePassword = false))
    }

    override fun insert(new: CustomerStatus) {
        repository.insert(new)
    }

    override fun delete(id: String) {
        repository.remove(CustomerStatus::username eq id)
    }

    override fun findById(id: String): CustomerStatus? {
        return repository.find(CustomerStatus::username eq id).firstOrDefault()
    }
}