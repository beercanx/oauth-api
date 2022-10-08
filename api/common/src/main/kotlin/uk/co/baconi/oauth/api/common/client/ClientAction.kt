package uk.co.baconi.oauth.api.common.client

enum class ClientAction(internal val value: String) {
    Introspect("introspect");
    companion object {
        fun fromValue(value: String): ClientAction = checkNotNull(values().firstOrNull { type -> type.value == value }) {
            "No such ClientAction with value [$value]"
        }
    }
}