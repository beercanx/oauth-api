package uk.co.baconi.oauth.api.assets

import io.ktor.server.locations.*

@Location("/assets")
object StaticAssetsLocation {

    @Location("/profile-images/{filename}")
    data class ProfileImagesLocation(val assets: StaticAssetsLocation, val filename: String) {
        constructor(filename: String) : this(StaticAssetsLocation, filename)
    }

}