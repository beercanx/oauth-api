import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

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
    }

    plugins.withId("org.jetbrains.kotlin.jvm") {
        // Replacement for kotlin { jvmToolchain(21) } in each project
        extensions.configure<KotlinJvmProjectExtension> {
            jvmToolchain(21)
        }
    }
}

tasks.withType<Wrapper>().configureEach {
    distributionType = Wrapper.DistributionType.ALL
}