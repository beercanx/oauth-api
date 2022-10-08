package uk.co.baconi.oauth.api.authentication

import org.slf4j.LoggerFactory
import uk.co.baconi.oauth.api.customer.CustomerMatch
import uk.co.baconi.oauth.api.customer.CustomerMatchService
import uk.co.baconi.oauth.api.customer.CustomerState
import uk.co.baconi.oauth.api.customer.CustomerStatusRepository
import uk.co.baconi.oauth.api.exchange.PasswordRequest

class AuthenticationService(
    private val customerMatchService: CustomerMatchService,
    private val customerStatusRepository: CustomerStatusRepository
) {

    companion object {
        private val logger = LoggerFactory.getLogger(AuthenticationService::class.java)
    }

    fun authenticate(request: AuthenticationRequest.Valid): Authentication = authenticate(
        username = request.username,
        password = request.password
    )

    fun authenticate(request: PasswordRequest): Authentication = authenticate(
        username = request.username,
        password = request.password
    )

    private fun authenticate(username: String, password: String): Authentication {

        return when (customerMatchService.match(username, password)) {
            is CustomerMatch.Missing -> Authentication.Failure
            is CustomerMatch.Mismatched -> Authentication.Failure
            is CustomerMatch.Success -> when (customerStatusRepository.findByUsername(username)?.state) {
                null -> {
                    logger.error("Unable to find customer status for $username")
                    Authentication.Failure
                }
                CustomerState.Suspended -> Authentication.Failure
                CustomerState.Closed -> Authentication.Failure
                CustomerState.ChangePassword -> TODO("Add support to change password on password match.")
                CustomerState.Active -> Authentication.Success(username)
            }
        }
    }
}