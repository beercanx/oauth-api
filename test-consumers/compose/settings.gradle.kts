rootProject.name = "compose"

include(":shared")
include(":android")
include(":desktop")
include(":website")

pluginManagement {

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

}
