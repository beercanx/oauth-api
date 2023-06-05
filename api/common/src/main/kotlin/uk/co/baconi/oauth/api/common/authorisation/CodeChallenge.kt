package uk.co.baconi.oauth.api.common.authorisation

data class CodeChallenge(val value: String) {
    override fun toString(): String = "CodeChallenge(value='REDACTED')"
}