package uk.co.baconi.oauth.api.common.customer

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object CustomerCredentialTable : IdTable<String>() {

    /**
     * [CustomerCredential.username]
     */
    override val id: Column<EntityID<String>> = varchar("id", 50).entityId() // TODO - Standardise the username field size somewhere
    override val primaryKey = PrimaryKey(id)

    val hashedSecret: Column<String> = varchar("hashed_secret", 60)
}