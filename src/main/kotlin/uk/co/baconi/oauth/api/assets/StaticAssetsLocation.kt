package uk.co.baconi.oauth.api.assets

import io.ktor.resources.*
import kotlinx.serialization.Serializable

@Serializable
@Resource("/assets")
class StaticAssetsLocation {

    @Serializable
    @Resource("/profile-images/{filename}")
    data class ProfileImagesLocation(val assets: StaticAssetsLocation = StaticAssetsLocation(), val filename: String) {
        constructor(filename: String) : this(StaticAssetsLocation(), filename)
    }

}