package uk.co.baconi.oauth.api.customer

sealed class CustomerMatch {
    data class Success(val username: String) : CustomerMatch()
    object Missing : CustomerMatch()
    object Mismatched : CustomerMatch()
}
