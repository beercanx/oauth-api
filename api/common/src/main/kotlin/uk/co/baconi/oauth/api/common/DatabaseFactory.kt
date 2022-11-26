package uk.co.baconi.oauth.api.common

import com.typesafe.config.ConfigFactory
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import uk.co.baconi.oauth.api.common.authentication.CustomerCredentialTable
import uk.co.baconi.oauth.api.common.authentication.CustomerStatusTable
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeTable
import uk.co.baconi.oauth.api.common.token.AccessTokenTable
import uk.co.baconi.oauth.api.common.token.RefreshTokenTable

object DatabaseFactory {

    private val logger = LoggerFactory.getLogger(DatabaseFactory::class.java)

    private val databaseConfiguration = ConfigFactory.load().getConfig("uk.co.baconi.oauth.api.database")

    val accessTokenDatabase: Database by lazy { connect("access-token", AccessTokenTable) }
    val authorisationCodeDatabase: Database by lazy { connect("authorisation-code", AuthorisationCodeTable) }
    val customerStatusDatabase: Database by lazy { connect("customer-status", CustomerStatusTable) }
    val customerCredentialDatabase: Database by lazy { connect("customer-credential", CustomerCredentialTable) }
    val refreshTokenDatabase: Database by lazy { connect("refresh-token", RefreshTokenTable) }

    private fun connect(name: String, table: Table, vararg tables: Table): Database {

        logger.info("Starting the '$name' database connection")

        val database = Database.connect(
            url = databaseConfiguration.getString("$name.url"),
            driver = databaseConfiguration.getString("$name.driver"),
            user = databaseConfiguration.getString("$name.user"),
            password = databaseConfiguration.getString("$name.password")
        )

        logger.debug("Connected to a '${database.vendor}' with version '${database.version}' on '${database.url}'")

        transaction(database) {
            SchemaUtils.create(table, *tables)
        }

        return database
    }
}