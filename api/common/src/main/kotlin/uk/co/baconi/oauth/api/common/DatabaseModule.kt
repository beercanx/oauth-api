package uk.co.baconi.oauth.api.common

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

object DatabaseModule {

    val accessTokenDB: Database by lazy {
        Database.connect(url = "jdbc:h2:mem:access_token;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")
    }

    /**
     * Intended to be used to force database initialisation at server start up but only when needed.
     */
    fun Application.accessTokenDatabase() {
        log.info("Registering the DatabaseModule.accessTokenDatabase() module")
        log.debug("Connected to a '${accessTokenDB.vendor}' with version '${accessTokenDB.version}' on '${accessTokenDB.url}'")
    }

}