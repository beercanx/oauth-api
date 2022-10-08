package uk.co.baconi.oauth.common.authentication

expect class CustomerAuthenticationService {
    suspend fun authenticate(username: String, password: CharArray): CustomerAuthentication
}