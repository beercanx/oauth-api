package uk.co.baconi.oauth.api.gatling

import io.gatling.javaapi.core.Session

fun sessionToString(field: String): (Session) -> String = { session -> session.sessionToString(field) }

fun Session.sessionToString(field: String): String = checkNotNull(getString(field)) {
    "$field was null in the session"
}