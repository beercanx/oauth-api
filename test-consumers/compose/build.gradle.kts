
plugins {
    // this is necessary to avoid the plugins to be loaded multiple times in each subproject's classloader
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.plugin.compose) apply false
    alias(libs.plugins.kotlin.plugin.serialization) apply false

    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.application) apply false

    alias(libs.plugins.jetbrains.compose) apply false
}

group = "uk.co.baconi.oauth.consumers.compose"

allprojects {

    repositories {
        google()
        mavenCentral()
    }

}
