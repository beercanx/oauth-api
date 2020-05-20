package com.sbgcore.oauth.api.openid

import com.sbgcore.oauth.api.authentication.AuthenticatedClient
import com.sbgcore.oauth.api.authentication.PkceClient
import io.ktor.application.Application
import io.ktor.application.log
import com.sbgcore.oauth.api.ktor.authenticate
import io.ktor.application.call
import io.ktor.auth.principal
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.request.receive
import io.ktor.routing.accept
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing

fun Application.openIdRoutesV2() {
    routing {
        trace { application.log.trace(it.buildText()) }
        route("/openid/v1/token_exchange") {
            accept(ContentType.Application.FormUrlEncoded) {
                authenticate<AuthenticatedClient> {
                    post {
                        val principal = call.principal<AuthenticatedClient>()
                        val request = call.receive<Parameters>()
                    }
                }
                authenticate<PkceClient> {
                    post {
                        val principal = call.principal<PkceClient>()
                        val request = call.receive<Parameters>()
                    }
                }
            }
        }
    }
}
