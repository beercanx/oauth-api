package uk.co.baconi.oauth.automation.api.config

class User(val username: String, val password: CharArray) {
    override fun toString() = "User(username='$username', password='REDACTED')"
}
