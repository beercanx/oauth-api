package uk.co.baconi.oauth.api.feeders

import io.gatling.core.Predef._
import io.gatling.core.session.{Expression, Session}

object Clients {

  private val CLIENT_ID = "clientId"
  private val CLIENT_SECRET = "clientSecret"

  object Expressions {

    val clientId: Expression[String] = session => session(CLIENT_ID).validate[String]

    val clientSecret: Expression[String] = session => session(CLIENT_SECRET).validate[String]

  }

  object Setup {

    def withClient(id: String, secret: String): Expression[Session] = session => session
      .set(CLIENT_ID, id)
      .set(CLIENT_SECRET, secret)

  }
}
