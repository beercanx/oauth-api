plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    kotlin("plugin.serialization")

    id("com.android.library")

    id("org.jetbrains.compose")
}

version = "1.0.0-SNAPSHOT"

val ktorVersion: String by project
val kotlinVersion: String by project
val kotlinxCoroutinesVersion: String by project
val kotlinxSerializationVersion: String by project

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

                api("io.ktor:ktor-client-core:$ktorVersion")
                api("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                api("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
                api("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerializationVersion")
            }
        }

        val androidMain by getting {
            dependencies {
                api("androidx.core:core-ktx:1.12.0")
                api("androidx.appcompat:appcompat:1.7.1")
                api("androidx.activity:activity-compose:1.8.2")
                api("androidx.datastore:datastore-preferences:1.0.0")

                api("io.ktor:ktor-client-okhttp:$ktorVersion")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinxCoroutinesVersion")
            }
        }

        val desktopMain by getting {
            dependencies {
                api(compose.desktop.common)

                api("io.ktor:ktor-client-cio:$ktorVersion")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-swing:$kotlinxCoroutinesVersion")

                api("io.ktor:ktor-server-core:$ktorVersion")
                api("io.ktor:ktor-server-netty:$ktorVersion")
            }
        }

        val jsMain by getting {
            dependencies {
                api("io.ktor:ktor-client-js:$ktorVersion")
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
