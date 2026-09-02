package uk.co.baconi.oauth.automation.api.config

data class User(val username: String, val password: String, val state: UserState) {
    override fun toString() = "User(username='$username', state='$state')"
}
