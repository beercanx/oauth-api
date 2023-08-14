package uk.co.baconi.oauth.api.common.authentication

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import uk.co.baconi.oauth.api.common.database.customColumn

fun Table.authenticatedUsernameColumn(): Column<AuthenticatedUsername> {
    return customColumn("username", 50, ::AuthenticatedUsername, AuthenticatedUsername::value)
}