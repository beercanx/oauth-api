val ktorVersion: String by project
val mockkVersion: String by project
val kotestVersion: String by project
val typesafeConfigVersion: String by project
val logbackVersion: String by project
val junitVersion: String by project
val exposedVersion: String by project
val h2DatabaseVersion: String by project
val argon2Version: String by project
val argon2Type: String by project
val commonsLang3Version: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {

    // Logging
    api("ch.qos.logback:logback-classic:$logbackVersion")

    // Configuration
    api("com.typesafe:config:$typesafeConfigVersion")

    // Server: Engine - using CIO as it supports JVM, Native and GraalVM but doesn't support HTTP/2
    api("io.ktor:ktor-server-cio:$ktorVersion")

    // Serialisation
    api("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // Server: Common
    api("io.ktor:ktor-server-core:$ktorVersion")
    api("io.ktor:ktor-server-auth:$ktorVersion")
    api("io.ktor:ktor-server-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-server-hsts:$ktorVersion")
    implementation("io.ktor:ktor-server-compression:$ktorVersion")
    implementation("io.ktor:ktor-server-double-receive:$ktorVersion")
    implementation("io.ktor:ktor-server-caching-headers:$ktorVersion")
    implementation("io.ktor:ktor-server-data-conversion:$ktorVersion")
    implementation("io.ktor:ktor-server-auto-head-response:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")

    // Crypto for safe credential checking
    implementation("de.mkammerer:$argon2Type:$argon2Version")

    // Database
    api("com.h2database:h2:$h2DatabaseVersion")
    api("org.jetbrains.exposed:exposed-core:$exposedVersion")
    api("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    api("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

    // JUnit 5 for tests definitions and running
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Asserting stuff
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")

    // Mocking
    testImplementation("io.mockk:mockk:$mockkVersion")

    // Test data generation
    testImplementation("org.apache.commons:commons-lang3:$commonsLang3Version")
}