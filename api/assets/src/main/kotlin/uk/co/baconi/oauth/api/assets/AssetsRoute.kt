package uk.co.baconi.oauth.api.assets

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

interface AssetsRoute {

    fun Route.assets() {

        application.log.info("Registering the AssetsRoute.assets() routes")

        staticResources(remotePath = "/assets/js", basePackage = "static/js", index = null)
    }
}