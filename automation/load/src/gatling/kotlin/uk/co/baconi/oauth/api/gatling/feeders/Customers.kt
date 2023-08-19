package uk.co.baconi.oauth.api.gatling.feeders

import io.gatling.javaapi.core.Session
import uk.co.baconi.oauth.api.gatling.sessionToString

object Customers {

    private const val USERNAME = "username"
    private const val PASSWORD = "password"

    object Expressions {
        val username: (Session) -> String = sessionToString(USERNAME)
        val password: (Session) -> String = sessionToString(PASSWORD)
    }

    object Feeders {

        private fun customer(username: String, password: String) = mapOf(
            USERNAME to username,
            PASSWORD to password
        )

        val customers: Array<Map<String, String>> = arrayOf(
            customer("AARDVARK", "121212"),
            customer("BADGER", "212121"),
            customer("ELEPHANT", "122112")
        )
    }
}