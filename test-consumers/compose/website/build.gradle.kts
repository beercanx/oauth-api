import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

val kotlinxCoroutinesVersion: String by project

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

compose.experimental {
    web.application {}
}
