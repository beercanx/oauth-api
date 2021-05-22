package uk.co.baconi.oauth.api.authentication

import org.slf4j.LoggerFactory
import uk.co.baconi.oauth.api.authentication.Authentication.Failure.Reason
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

        return when (val match = customerMatchService.match(username, password)) {
            is CustomerMatch.Missing -> Authentication.Failure(Reason.Missing)
            is CustomerMatch.Mismatched -> Authentication.Failure(Reason.Mismatched)
            is CustomerMatch.Success -> when (customerStatusRepository.findByUsername(match.username)?.state) { // TODO - Consider using typed username to prevent issues with case.
                null -> {
                    logger.error("Unable to find customer status for $username")
                    Authentication.Failure(Reason.Missing)
                }
                CustomerState.Suspended -> Authentication.Failure(Reason.Suspended)
                CustomerState.Closed -> Authentication.Failure(Reason.Closed)
                CustomerState.ChangePassword -> Authentication.Failure(Reason.ChangePassword) // TODO - Add support to change password on password match.
                CustomerState.Active -> Authentication.Success(match.username) // TODO - Consider using typed username to prevent issues with case.
            }
        }
    }
}