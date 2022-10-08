package uk.co.baconi.oauth.api.swagger

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

interface SwaggerRoute {

    fun Route.swagger() {
        static("/docs") {
            static("/") {
                resources("swagger-ui.v3.44.1")
                resource("swagger.yaml")
                get {
                    call.respondRedirect("/docs/index.html")
                }
            }
            get {
                call.respondRedirect("/docs/index.html")
            }
        }
    }
}