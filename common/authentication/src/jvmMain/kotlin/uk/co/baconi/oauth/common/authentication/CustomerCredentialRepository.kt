package uk.co.baconi.oauth.common.authentication

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class CustomerCredentialRepository(private val database: Database) {

    fun insert(new: CustomerCredential) {
        transaction(database) {
            CustomerCredentialTable.insertAndGetId {
                it[id] = new.username.lowercase()
                it[hashedSecret] = new.hashedSecret
            }
        }
    }

    fun findByUsername(username: String): CustomerCredential? {
        return transaction(database) {
            CustomerCredentialTable
                .select { CustomerCredentialTable.id eq username.lowercase() }
                .firstOrNull()
                ?.let(::toCustomerCredential)
        }
    }

    private fun toCustomerCredential(it: ResultRow): CustomerCredential {
        return CustomerCredential(
            username = it[CustomerCredentialTable.id].value,
            hashedSecret = it[CustomerCredentialTable.hashedSecret]
        )
    }
}