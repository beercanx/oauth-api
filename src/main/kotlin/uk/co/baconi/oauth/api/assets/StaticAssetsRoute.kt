package uk.co.baconi.oauth.api.assets

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import uk.co.baconi.oauth.api.ktor.ApplicationContext

interface StaticAssetsRoute {

    fun Route.staticAssets() {

        // Specific, for generated urls
        get<StaticAssetsLocation.ProfileImagesLocation> { image ->
            returnContent(image.filename, "assets/profile-images")
        }

        // General, fallback offering
        resource<StaticAssetsLocation> {
            resources("assets")
        }
    }

    private suspend fun ApplicationContext.returnContent(path: String, basePath: String?) {
        val content = call.resolveResource(path, basePath)
        if (content != null) {
            call.respond(content)
        }
    }
}