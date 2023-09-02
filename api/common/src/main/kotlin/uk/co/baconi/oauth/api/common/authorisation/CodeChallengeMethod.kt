package uk.co.baconi.oauth.api.common.authorisation

/**
 * Supported code challenge methods, DO NOT add plain!
 */
enum class CodeChallengeMethod {
    S256;

    companion object {
        fun fromNameOrNull(name: String): CodeChallengeMethod? = entries.firstOrNull { type ->
            type.name == name
        }
    }
}
