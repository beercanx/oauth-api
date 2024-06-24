package uk.co.baconi.oauth.api.gatling.feeders

import io.gatling.javaapi.core.Session
import uk.co.baconi.oauth.api.gatling.sessionToString
import java.util.UUID

object State {

    private const val STATE = "state"

    object Expressions {
        val state: (Session) -> String = sessionToString(STATE)
    }

    object Setup {

        fun withState(state: String? = null): (Session) -> Session = { session ->
            session.set(STATE, state ?: UUID.randomUUID().toString())
        }
    }
}