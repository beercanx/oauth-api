package uk.co.baconi.oauth.api.common.database

import org.jetbrains.exposed.sql.IColumnType
import org.jetbrains.exposed.sql.VarCharColumnType

/**
 * Based on https://jetbrains.github.io/Exposed/frequently-asked-questions.html#q-how-can-i-create-a-custom-column-type.
 *
 * Supports converting a single field from say a data class into a String value in the database.
 */
class CustomColumnType<T : Any>(
    length: Int,
    private val constructor: (String) -> T,
    private val destructor: (T) -> String
) : IColumnType<T> {

    private val varCharColType: VarCharColumnType = VarCharColumnType(length)

    override var nullable: Boolean = varCharColType.nullable
    override fun sqlType(): String = varCharColType.sqlType()
    override fun valueFromDB(value: Any): T = constructor(varCharColType.valueFromDB(value))
    override fun notNullValueToDB(value: T): Any = varCharColType.notNullValueToDB(destructor(value))
    override fun nonNullValueToString(value: T): String = varCharColType.nonNullValueToString(destructor(value))

}