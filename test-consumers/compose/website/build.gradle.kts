import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

val kotlinxCoroutinesVersion: String by project

kotlin {

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "compose-website"
        binaries.executable()
        browser {
            commonWebpackConfig {
                outputFileName = "$moduleName.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).copy(
                    static = (devServer?.static ?: mutableListOf()).apply {
                        add(project.rootDir.path)
                    }
                )
            }
        }
    }

    sourceSets {
        val wasmJsMain by getting {
            dependencies {
                implementation(project(":shared"))
            }
        }
    }
}

compose.experimental {
    web.application {}
}

// WORKAROUND for js-joda that's incorrectly pulled in for WASM by a dependency.
tasks {
    val hackNodeModuleImports by registering(Copy::class) {
        group = "kotlin browser"
        mustRunAfter(":kotlinNpmInstall")
        from(rootProject.buildDir.path + "/js/node_modules/@js-joda/core/dist/js-joda.esm.js")
        into(rootProject.buildDir.path + "/js/packages/compose-website/kotlin/@js-joda/core/dist/")
    }
    for (dependent in listOf("wasmJsBrowserProductionRun", "wasmJsBrowserDevelopmentRun")) {
        named(dependent) {
            dependsOn(hackNodeModuleImports)
        }
    }
}