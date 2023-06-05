package uk.co.baconi.oauth.api.common.client

enum class ClientAction(internal val value: String) {
    Authorise("authorise"),
    Introspect("introspect"),
    ProofKeyForCodeExchange("pkce"),
    ;
    companion object {
        fun fromValue(value: String): ClientAction = checkNotNull(values().firstOrNull { type -> type.value == value }) {
            "No such ClientAction with value [$value]"
        }
    }
}