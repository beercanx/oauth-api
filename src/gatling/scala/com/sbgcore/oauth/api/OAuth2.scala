package com.sbgcore.oauth.api

import io.gatling.core.session.Expression

object OAuth2 {

  object Expressions {

    /**
     * Generates the bearer token value for an Authorization header.
     */
    def bearerAuth(token: Expression[String]): Expression[String] = session => for {
      token <- token(session)
    } yield s"""Bearer $token"""

  }

}
