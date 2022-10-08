package uk.co.baconi.oauth.api.common

import com.typesafe.config.ConfigFactory
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationCodeTable
import uk.co.baconi.oauth.api.common.token.AccessTokenTable
import uk.co.baconi.oauth.common.authentication.CustomerCredentialTable
import uk.co.baconi.oauth.common.authentication.CustomerStatusTable

object DatabaseFactory {

    private val logger = LoggerFactory.getLogger(DatabaseFactory::class.java)

    private val databaseConfiguration = ConfigFactory.load().getConfig("uk.co.baconi.oauth.api.database")

    private val accessTokenDatabase: Lazy<Database> = lazy {
        Database.connect(
            url = databaseConfiguration.getString("access-token.url"),
            driver = databaseConfiguration.getString("access-token.driver"),
            user = databaseConfiguration.getString("access-token.user"),
            password = databaseConfiguration.getString("access-token.password")
        )
    }

    private val authorisationCodeDatabase: Lazy<Database> = lazy {
        Database.connect(
            url = databaseConfiguration.getString("authorisation-code.url"),
            driver = databaseConfiguration.getString("authorisation-code.driver"),
            user = databaseConfiguration.getString("authorisation-code.user"),
            password = databaseConfiguration.getString("authorisation-code.password")
        )
    }

    private val customerStatusDatabase: Lazy<Database> = lazy {
        Database.connect(
            url = databaseConfiguration.getString("customer-status.url"),
            driver = databaseConfiguration.getString("customer-status.driver"),
            user = databaseConfiguration.getString("customer-status.user"),
            password = databaseConfiguration.getString("customer-status.password")
        )
    }

    private val customerCredentialDatabase: Lazy<Database> = lazy {
        Database.connect(
            url = databaseConfiguration.getString("customer-credential.url"),
            driver = databaseConfiguration.getString("customer-credential.driver"),
            user = databaseConfiguration.getString("customer-credential.user"),
            password = databaseConfiguration.getString("customer-credential.password")
        )
    }

    /**
     * Intended to be used to force database initialisation at server start up but only when needed.
     */
    fun getAccessTokenDatabase(): Database {

        if(accessTokenDatabase.isInitialized()) {
            return accessTokenDatabase.value
        }

        logger.info("Starting the AccessToken database connection")

        val database = accessTokenDatabase.value

        transaction(database) {
            SchemaUtils.create(AccessTokenTable)
        }

        logger.debug("Connected to a '${database.vendor}' with version '${database.version}' on '${database.url}'")

        return database
    }

    fun getAuthorisationCodeDatabase(): Database {

        if(authorisationCodeDatabase.isInitialized()) {
            return authorisationCodeDatabase.value
        }

        logger.info("Starting the AuthorisationCode database connection")

        val database = authorisationCodeDatabase.value

        transaction(database) {
            SchemaUtils.create(AuthorisationCodeTable)
        }

        logger.debug("Connected to a '${database.vendor}' with version '${database.version}' on '${database.url}'")

        return database
    }

    fun getCustomerStatusDatabase(): Database {

        if(customerStatusDatabase.isInitialized()) {
            return customerStatusDatabase.value
        }

        logger.info("Starting the CustomerStatus database connection")

        val database = customerStatusDatabase.value

        transaction(database) {
            SchemaUtils.create(CustomerStatusTable)
        }

        logger.debug("Connected to a '${database.vendor}' with version '${database.version}' on '${database.url}'")

        return database
    }

    fun getCustomerCredentialDatabase(): Database {

        if(customerCredentialDatabase.isInitialized()) {
            return customerCredentialDatabase.value
        }

        logger.info("Starting the CustomerCredential database connection")

        val database = customerCredentialDatabase.value

        transaction(database) {
            SchemaUtils.create(CustomerCredentialTable)
        }

        logger.debug("Connected to a '${database.vendor}' with version '${database.version}' on '${database.url}'")

        return database
    }
}