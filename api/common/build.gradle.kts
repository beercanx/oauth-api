val useArgon2NoLibs: String by project

plugins {
    jacoco
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {

    // Logging
    api(libs.logback.classic)

    // Configuration
    api(libs.typesafe.config)

    // Server: Engine - using CIO as it supports JVM, Native and GraalVM but doesn't support HTTP/2
    api(libs.ktor.server.cio)

    // Serialisation
    api(libs.ktor.serialization.kotlinx.json)

    // Server: Common
    api(libs.ktor.server.core)
    api(libs.ktor.server.auth)
    api(libs.ktor.server.sessions)
    api(libs.ktor.server.html)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.hsts)
    implementation(libs.ktor.server.compression)
    implementation(libs.ktor.server.double.receive)
    implementation(libs.ktor.server.caching.headers)
    implementation(libs.ktor.server.data.conversion)
    implementation(libs.ktor.server.auto.head.response)
    implementation(libs.ktor.server.content.negotiation)

    // Crypto for safe credential checking
    api(if (useArgon2NoLibs.toBooleanStrict()) libs.argon2.jvm.nolibs else libs.argon2.jvm.libs)

    // Database
    api(libs.h2database)
    api(libs.exposed.core)
    api(libs.exposed.jdbc)
    api(libs.exposed.java.time)

    // JUnit 5 for tests definitions and running
    testImplementation(enforcedPlatform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Ktor testing
    testImplementation(libs.ktor.server.test.host)

    // Asserting stuff
    testImplementation(libs.kotest.assertions)

    // Mocking
    testImplementation(libs.mockk)

    // Test data generation
    testImplementation(libs.commons.lang3)

    // Security patching
    constraints {
        api("commons-codec:commons-codec:1.16.0") {
            because("Apache Http Client brings in 1.11")
        }
    }
}