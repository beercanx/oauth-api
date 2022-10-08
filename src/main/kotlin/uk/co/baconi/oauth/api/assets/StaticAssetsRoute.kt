package uk.co.baconi.oauth.api.assets

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import uk.co.baconi.oauth.api.ktor.ApplicationContext

interface StaticAssetsRoute {

    fun Route.staticAssets() {

        // Specific, for generated urls
        get<StaticAssetsLocation.ProfileImagesLocation> { image ->
            returnContent(image.filename, "assets/profile-images")
        }

        // General, fallback offering
        location<StaticAssetsLocation> {
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