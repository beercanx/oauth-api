package com.sbgcore.oauth.api.customer.internal

import com.sbgcore.oauth.api.customer.internal.CustomerState.Active
import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.getRepository
import org.dizitart.kno2.nitrite
import org.dizitart.no2.Nitrite

class NitriteCustomerStatusRepository(database: Nitrite) : CustomerStatusRepository {

    /**
     * Create a new instance of [NitriteCustomerStatusRepository] with an in-memory instance of [Nitrite]
     */
    constructor() : this(
        nitrite(userId = "internal-customer-status", password = ":_,z;&b7&]u{E'!&fz<PL35NvqTKx'5")
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