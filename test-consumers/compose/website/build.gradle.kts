plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
}

kotlin {

    js(IR) {
        moduleName = "compose-website"
        binaries.executable()
        browser {
            commonWebpackConfig {
                outputFileName = "$moduleName.js"
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
