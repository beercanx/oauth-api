rootProject.name = "compose"

include(":shared")
include(":android")
include(":desktop")
include(":website")

// Here, because workaround https://github.com/gradle/gradle/issues/1697#issuecomment-655682357
pluginManagement {

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        val agpVersion: String by settings
        val kotlinVersion: String by settings
        val composeWasmVersion: String by settings

        kotlin("jvm") version kotlinVersion apply false
        kotlin("android") version kotlinVersion apply false
        kotlin("multiplatform") version kotlinVersion apply false
        kotlin("plugin.serialization") version kotlinVersion apply false

        id("com.android.base") version agpVersion apply false
        id("com.android.library") version agpVersion apply false
        id("com.android.application") version agpVersion apply false

        id("org.jetbrains.compose") version composeWasmVersion apply false
    }
}
