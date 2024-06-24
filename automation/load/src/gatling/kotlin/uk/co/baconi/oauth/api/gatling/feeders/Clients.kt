package uk.co.baconi.oauth.api.gatling.feeders

import io.gatling.javaapi.core.Session
import uk.co.baconi.oauth.api.gatling.sessionToString

object Clients {

    private const val CLIENT_ID = "clientId"
    private const val CLIENT_SECRET = "clientSecret"
    private const val CLIENT_REDIRECT = "clientRedirect"

    object Expressions {
        val clientId: (Session) -> String = sessionToString(CLIENT_ID)
        val clientSecret: (Session) -> String = sessionToString(CLIENT_SECRET)
        val clientRedirect: (Session) -> String = sessionToString(CLIENT_REDIRECT)
    }

    object Setup {

        fun withClient(id: String, secret: String, redirect: String?): (Session) -> Session = { session ->
            session
                .set(CLIENT_ID, id)
                .set(CLIENT_SECRET, secret)
                .set(CLIENT_REDIRECT, redirect)
        }
    }
}