package uk.co.baconi.oauth.api.common.authentication

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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
                .selectAll()
                .where { CustomerCredentialTable.id eq username.lowercase() }
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