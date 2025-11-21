plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.plugin.compose)
    alias(libs.plugins.kotlin.plugin.serialization)

    alias(libs.plugins.android.library)

    alias(libs.plugins.jetbrains.compose)
}

version = "1.0.0-SNAPSHOT"

kotlin {

    jvmToolchain(11)

    androidTarget()

    jvm("desktop")

    js(IR) {
        browser()
    }

    sourceSets {

        val commonMain by getting {
            dependencies {
                api(compose.ui)
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.material3)
                api(compose.components.resources)

                api(libs.ktor.client.core)
                api(libs.ktor.client.content.negotiation)
                api(libs.ktor.serialization.kotlinx.json)

                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.serialization.core)
            }
        }

        val androidMain by getting {
            dependencies {
                api(libs.androidx.core.ktx)
                api(libs.androidx.appcompat)
                api(libs.androidx.activity.compose)
                api(libs.androidx.datastore.preferences)

                api(libs.ktor.client.okhttp)
                api(libs.kotlinx.coroutines.android)
            }
        }

        val desktopMain by getting {
            dependencies {
                api(compose.desktop.common)

                api(libs.ktor.client.cio)
                api(libs.kotlinx.coroutines.swing)

                api(libs.ktor.server.core)
                api(libs.ktor.server.netty)
            }
        }

        val jsMain by getting {
            dependencies {
                api(libs.ktor.client.js)
            }
        }
    }
}

android {
    compileSdk = 35
    namespace = "uk.co.baconi.compose.shared"
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
