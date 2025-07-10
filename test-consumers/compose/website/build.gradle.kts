plugins {
    kotlin("multiplatform")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
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
