package uk.co.baconi.oauth.api.authentication

import io.ktor.server.routing.*
import uk.co.baconi.oauth.api.common.CommonModule.common
import uk.co.baconi.oauth.api.common.DatabaseModule
import uk.co.baconi.oauth.api.common.TestUserModule
import uk.co.baconi.oauth.api.common.authentication.CustomerAuthenticationService
import uk.co.baconi.oauth.api.common.authentication.CustomerCredentialRepository
import uk.co.baconi.oauth.api.common.authentication.CustomerStatusRepository
import uk.co.baconi.oauth.api.common.embeddedCommonServer

/**
 * Start a server for just Authentication requests
 */
internal object AuthenticationServer : DatabaseModule, AuthenticationRoute, TestUserModule {

    override val customerCredentialRepository = CustomerCredentialRepository(customerCredentialDatabase)
    override val customerStatusRepository = CustomerStatusRepository(customerStatusDatabase)
    override val customerAuthenticationService = CustomerAuthenticationService(customerCredentialRepository, customerStatusRepository)

    fun start() {
        embeddedCommonServer {
            common()
            routing {
                authentication()
            }
            generateTestUsers()
        }.start(true)
    }
}