package uk.co.baconi.oauth.api.common.scope

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import uk.co.baconi.oauth.api.common.database.customColumn
import uk.co.baconi.oauth.api.common.scope.ScopeRepository.Companion.maxScopeFieldLength

private object ScopeTableSerializer : SpaceDelimitedSerializer<Scope>() {
    override fun encode(value: Scope): String = value.value
    override fun decode(string: String): Scope = Scope(string)
}

// TODO - Consider true DB style, with a table of scopes and references in a list format
fun Table.scopeColumn(name: String): Column<Set<Scope>> {
    return customColumn(name, maxScopeFieldLength, ScopeTableSerializer::deserialize, ScopeTableSerializer::serialize)
}