plugins {
    kotlin("multiplatform")

    id("com.android.application")

    id("org.jetbrains.compose")
}

kotlin {
    jvmToolchain(11)
    androidTarget()
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(project(":shared"))
            }
        }
    }
}

android {
    compileSdk = 34
    namespace = "uk.co.baconi.compose.android"
    defaultConfig {
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
