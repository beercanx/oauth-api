val ktorVersion: String by project
val mockkVersion: String by project
val kotestVersion: String by project
val datetimeVersion: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    // Logging
    // TODO - implementation("ch.qos.logback:logback-classic:1.2.11")

    // Configuration
    // TODO - implementation("com.typesafe:config:1.4.2")

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-resources:$ktorVersion")
    implementation("io.ktor:ktor-server-data-conversion:$ktorVersion")
    implementation("io.ktor:ktor-server-auto-head-response:$ktorVersion")
    implementation("io.ktor:ktor-server-hsts:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-compression:$ktorVersion") // TODO - Add in the config
    implementation("io.ktor:ktor-server-caching-headers:$ktorVersion")
    implementation("io.ktor:ktor-server-double-receive:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")

    // Crypto for safe password checking
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")

    testImplementation(kotlin("test-common"))
    testImplementation(kotlin("test-annotations-common"))

    // TODO - Replace kotlin.test runner?
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")

    testImplementation("io.mockk:mockk-common:$mockkVersion")
}