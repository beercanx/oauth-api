package uk.co.baconi.oauth.api.common

import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import uk.co.baconi.oauth.api.common.token.AccessTokenTable

object DatabaseModule {

    private val databaseConfiguration = ConfigFactory.load().getConfig("uk.co.baconi.oauth.api.database")

    // TODO - Stop using singleton and move to injection pattern
    val accessTokenDatabase: Database by lazy {
        Database.connect(
            url = databaseConfiguration.getString("access-token.url"),
            driver = databaseConfiguration.getString("access-token.driver"),
            user = databaseConfiguration.getString("access-token.user"),
            password = databaseConfiguration.getString("access-token.password")
        )
    }

    /**
     * Intended to be used to force database initialisation at server start up but only when needed.
     */
    fun Application.accessTokenDatabase() {
        log.info("Registering the DatabaseModule.accessTokenDatabase() module")

        // Crude but affective way to make sure the database is configured.
        transaction(accessTokenDatabase) {
            SchemaUtils.create(AccessTokenTable)
        }

        log.debug("Connected to a '${accessTokenDatabase.vendor}' with version '${accessTokenDatabase.version}' on '${accessTokenDatabase.url}'")
    }

}