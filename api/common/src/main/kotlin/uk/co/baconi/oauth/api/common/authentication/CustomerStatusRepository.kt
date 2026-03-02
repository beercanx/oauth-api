package uk.co.baconi.oauth.api.common.authentication

import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class CustomerStatusRepository(private val database: Database) {

    fun insert(new: CustomerStatus) {
        transaction(database) {
            CustomerStatusTable.insertAndGetId {
                it[id] = new.username.lowercase()
                it[state] = new.state
            }
        }
    }

    fun findByUsername(username: String): CustomerStatus? {
        return transaction(database) {
            CustomerStatusTable
                .selectAll()
                .where { CustomerStatusTable.id eq username.lowercase() }
                .firstOrNull()
                ?.let(::toCustomerStatus)
        }
    }

    private fun toCustomerStatus(it: ResultRow): CustomerStatus {
        return CustomerStatus(
            username = it[CustomerStatusTable.id].value,
            state = it[CustomerStatusTable.state]
        )
    }
}