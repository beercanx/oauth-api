package uk.co.baconi.oauth.api.common.database

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

fun <T : Any> Table.customColumn(
    name: String,
    length: Int,
    constructor: (String) -> T,
    destructor: (T) -> String
): Column<T> = registerColumn(name, CustomColumnType(length, constructor, destructor))