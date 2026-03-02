package uk.co.baconi.oauth.api.common.client

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import uk.co.baconi.oauth.api.common.database.customColumn

fun Table.clientIdColumn(name: String): Column<ClientId> {
    return customColumn(name, 25, ::ClientId, ClientId::value)
}