package uk.co.baconi.oauth.automation.api.config

data class User(val username: String, val password: String) {
    override fun toString() = "User(username='$username', password='REDACTED')"
}
