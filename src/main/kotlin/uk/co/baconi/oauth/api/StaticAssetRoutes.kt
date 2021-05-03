package uk.co.baconi.oauth.api

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import uk.co.baconi.oauth.api.ktor.ApplicationContext

@Location("/assets")
object Assets {

    @Location("/profile-images/{filename}")
    data class ProfileImages(val filename: String)

}

interface StaticAssetRoutes {

    fun Route.staticAssets() {

        // Specific, for generated urls
        get<Assets.ProfileImages> { image ->
            returnContent(image.filename, "assets/profile-images")
        }

        // General, fallback offering
        static("assets") {
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