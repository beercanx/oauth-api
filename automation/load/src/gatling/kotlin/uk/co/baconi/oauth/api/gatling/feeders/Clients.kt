package uk.co.baconi.oauth.api.gatling.feeders

import io.gatling.javaapi.core.Session
import uk.co.baconi.oauth.api.gatling.feeders.Clients.Client.Type.Confidential
import uk.co.baconi.oauth.api.gatling.feeders.Clients.Client.Type.Public

object Clients {

    private const val CLIENT = "client"

    data class Client(val id: String, val secret: String?, val redirect: String, val type: Type) {
        enum class Type {
            Confidential,
            Public;
        }
    }

    object Expressions {
        val client: (Session) -> Client = { session -> checkNotNull(session.get<Client>(CLIENT)) { "client was null" } }
        val clientId: (Session) -> String = { session -> client(session).id }
        val clientSecret: (Session) -> String? = { session -> client(session).secret }
        val clientRedirect: (Session) -> String = { session -> client(session).redirect }
    }

    object Setup {

        fun withConfidentialClient(id: String, secret: String, redirect: String): (Session) -> Session = { session ->
            session.set(CLIENT, Client(id, secret, redirect, Confidential))
        }

        fun withPublicClient(id: String, redirect: String): (Session) -> Session = { session ->
            session.set(CLIENT, Client(id, null, redirect, Public))
        }
    }
}