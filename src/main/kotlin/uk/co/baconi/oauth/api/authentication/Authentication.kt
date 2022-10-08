package uk.co.baconi.oauth.api.authentication

sealed class Authentication {

    data class Success(val username: String) : Authentication()

    // TODO - Work out what data we need to return
    object Failure : Authentication()
}
