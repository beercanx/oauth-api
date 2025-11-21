plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.plugin.compose)
    alias(libs.plugins.jetbrains.compose)
}

kotlin {

    js(IR) {
        outputModuleName = "compose-website"
        binaries.executable()
        browser {
            commonWebpackConfig {
                outputFileName = "${outputModuleName.get()}.js"
            }
        }
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":shared"))
            }
        }
    }
}
