import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times in each subproject's classloader
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false

    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false

    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.composeMultiplatform) apply false
}

group = "uk.co.baconi.oauth.consumers.compose"

// Security patching JavaScript dependencies via Yarn
allprojects {
    plugins.withType<YarnPlugin> {
        extensions.configure<YarnRootExtension> {
            resolution("diff", "8.0.3")
            resolution("uuid", "11.1.1")
            resolution("ws", "8.21.0")
            resolution("serialize-javascript", "7.0.5") // Review once Mocha 12 is released and used.
            resolution("webpack", "5.104.1")
            resolution("webpack-dev-server", "5.2.5")
        }
    }
}
