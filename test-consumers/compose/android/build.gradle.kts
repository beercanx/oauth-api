plugins {
    alias(libs.plugins.androidApplication)

    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
}

kotlin {
    jvmToolchain(11)
    dependencies {
        implementation(projects.shared)
    }
}

android {
    compileSdk = 36
    namespace = "uk.co.baconi.compose.android"
    defaultConfig {
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
