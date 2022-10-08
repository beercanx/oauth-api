package uk.co.baconi.oauth.api.common

import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

object DatabaseModule {

    private val databaseConfiguration = ConfigFactory.load().getConfig("uk.co.baconi.oauth.api.database")

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
        log.debug("Connected to a '${accessTokenDatabase.vendor}' with version '${accessTokenDatabase.version}' on '${accessTokenDatabase.url}'")
    }

}