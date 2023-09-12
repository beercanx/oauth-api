import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

val kotlinxCoroutinesVersion: String by project

kotlin {

    @OptIn(ExperimentalWasmDsl::class)
    wasm {
        browser()
        binaries.executable()
    }

    sourceSets {
        val wasmMain by getting {
            dependencies {
                implementation(project(":shared"))
            }
        }
    }
}

compose.experimental {
    web.application {}
}

compose {
    val composeWasmVersion: String by project
    kotlinCompilerPlugin.set(composeWasmVersion)

    // Fixes lintVitalAnalyzeRelease and lintAnalyzeDebug
    val kotlinVersion: String by project
    kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=$kotlinVersion")
}