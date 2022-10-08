package uk.co.baconi.oauth.api.common.customer

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import java.util.*

object CustomerStatusTable : IdTable<String>() {

    /**
     * [CustomerStatus.username]
     */
    override val id: Column<EntityID<String>> = varchar("id", 50).entityId() // TODO - Standardise the username field size somewhere
    override val primaryKey = PrimaryKey(id)

    val state: Column<CustomerState> = enumerationByName("state", maxFieldLength<CustomerState>())

    private inline fun <reified E : Enum<E>> maxFieldLength(): Int {
        return enumValues<E>().map(Enum<E>::name).maxOfOrNull(String::length) ?: 0
    }
}