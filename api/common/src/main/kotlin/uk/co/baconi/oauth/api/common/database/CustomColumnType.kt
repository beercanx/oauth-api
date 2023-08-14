package uk.co.baconi.oauth.api.common.database

import org.jetbrains.exposed.sql.VarCharColumnType

/**
 * Based on https://github.com/JetBrains/Exposed/wiki/FAQ#q-how-create-custom-collumn-type.
 *
 * Supports converting a single field from say a data class into a String value in the database.
 */
class CustomColumnType<T : Any>(
    length: Int,
    private val constructor: (String) -> T,
    private val destructor: (T) -> String
) : VarCharColumnType(length) {

    override fun valueFromDB(value: Any): T = constructor(value as String)

    @Suppress("UNCHECKED_CAST")
    override fun notNullValueToDB(value: Any): String = destructor(value as T)
}