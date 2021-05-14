package uk.co.baconi.oauth.api.swagger

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*

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