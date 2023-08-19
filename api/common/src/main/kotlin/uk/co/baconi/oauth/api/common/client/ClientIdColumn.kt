package uk.co.baconi.oauth.api.common.client

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import uk.co.baconi.oauth.api.common.database.customColumn

fun Table.clientIdColumn(): Column<ClientId> {
    return customColumn("client_id", 25, ::ClientId, ClientId::value)
}