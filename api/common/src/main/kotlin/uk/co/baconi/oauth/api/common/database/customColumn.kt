package uk.co.baconi.oauth.api.common.database

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table

fun <T : Any> Table.customColumn(
    name: String,
    length: Int,
    constructor: (String) -> T,
    destructor: (T) -> String
): Column<T> = registerColumn(name, CustomColumnType(length, constructor, destructor))