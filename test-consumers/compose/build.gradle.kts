import org.jetbrains.compose.ComposeExtension

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times in each subproject's classloader
    kotlin("jvm") apply false
    kotlin("android") apply false
    kotlin("multiplatform") apply false

    id("com.android.library") apply false
    id("com.android.application") apply false

    id("org.jetbrains.compose") apply false
}

group = "uk.co.baconi.oauth.consumers.compose"

allprojects {

    repositories {
        google()
        mavenCentral()
        //maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
    }

    afterEvaluate {
        extensions.findByType(ComposeExtension::class.java)?.apply {

            val composeCompilerVersion: String by project
            kotlinCompilerPlugin.set(composeCompilerVersion)

            val kotlinVersion: String by project
            kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=$kotlinVersion")
        }
    }
}

tasks.withType<Wrapper>().configureEach {
    distributionType = Wrapper.DistributionType.ALL
}
