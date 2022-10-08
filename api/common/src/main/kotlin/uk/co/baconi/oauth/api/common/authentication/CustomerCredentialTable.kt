package uk.co.baconi.oauth.api.common.authentication

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object CustomerCredentialTable : IdTable<String>() {

    /**
     * [CustomerCredential.username]
     */
    override val id: Column<EntityID<String>> = varchar("id", 50).entityId() // TODO - Standardise the username field size somewhere
    override val primaryKey = PrimaryKey(id)

    /**
     * [CustomerCredential.hashedSecret]
     */
    val hashedSecret: Column<String> = varchar("hashed_secret", 100) // TODO - Update for argon2id lengths + salt + config
}