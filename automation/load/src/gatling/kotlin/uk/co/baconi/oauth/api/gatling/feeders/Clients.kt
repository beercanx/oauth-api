package uk.co.baconi.oauth.api.gatling.feeders

import io.gatling.javaapi.core.Session
import uk.co.baconi.oauth.api.gatling.sessionToString

object Clients {

    private const val CLIENT_ID = "clientId"
    private const val CLIENT_SECRET = "clientSecret"

    object Expressions {
        val clientId: (Session) -> String = sessionToString(CLIENT_ID)
        val clientSecret: (Session) -> String = sessionToString(CLIENT_SECRET)
    }

    object Setup {

        // TODO - Decide on which pattern we want to use more Function<Session, Session> or Session.withClient()

        fun withClient(id: String, secret: String): (Session) -> Session = { session ->
            session.withClient(id, secret)
        }

        fun Session.withClient(id: String, secret: String): Session = this
            .set(CLIENT_ID, id)
            .set(CLIENT_SECRET, secret)
    }
}