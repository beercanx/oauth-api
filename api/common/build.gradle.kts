val ktorVersion: String by project
val mockkVersion: String by project
val kotestVersion: String by project
val typesafeConfigVersion: String by project
val bouncyCastleVersion: String by project
val logbackVersion: String by project
val junitVersion: String by project
val exposedVersion: String by project
val h2DatabaseVersion: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    api(kotlin("stdlib-jdk8"))
    api(kotlin("reflect"))

    // Logging
    api("ch.qos.logback:logback-classic:$logbackVersion")

    // Configuration
    api("com.typesafe:config:$typesafeConfigVersion")

    // Server: Engine - using CIO as it supports JVM, Native and GraalVM but doesn't support HTTP/2
    api("io.ktor:ktor-server-cio:$ktorVersion")

    // Server: Common
    api("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-resources:$ktorVersion")
    implementation("io.ktor:ktor-server-data-conversion:$ktorVersion")
    implementation("io.ktor:ktor-server-auto-head-response:$ktorVersion")
    implementation("io.ktor:ktor-server-hsts:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-compression:$ktorVersion") // TODO - Removing this could enable a Native first server
    implementation("io.ktor:ktor-server-caching-headers:$ktorVersion")
    implementation("io.ktor:ktor-server-double-receive:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")

    // Crypto for safe password checking
    implementation("org.bouncycastle:bcprov-jdk15on:$bouncyCastleVersion")

    // Database - TODO - Removing this could enable a Native first server
    implementation("com.h2database:h2:$h2DatabaseVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

    // JUnit 5 for tests definitions and running
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")

    // Asserting stuff
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")

    // Mocking
    testImplementation("io.mockk:mockk:$mockkVersion")
}