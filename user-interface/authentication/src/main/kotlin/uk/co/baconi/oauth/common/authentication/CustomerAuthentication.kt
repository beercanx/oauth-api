package uk.co.baconi.oauth.common.authentication

external interface CustomerAuthentication {
    val type: String?
    val username: String?
}
