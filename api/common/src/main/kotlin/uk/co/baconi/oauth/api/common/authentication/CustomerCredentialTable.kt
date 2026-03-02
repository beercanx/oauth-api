package uk.co.baconi.oauth.api.common.authentication

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.core.Column

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