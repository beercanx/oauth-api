plugins {
    alias(libs.plugins.kotlinMultiplatform)

    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
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
                implementation(projects.shared)
                implementation(devNpm("webpack", "5.104.1")) // Added to allow security patching
            }
        }
    }
}
