val ktorVersion: String by project
val mockkVersion: String by project
val kotestVersion: String by project
val typesafeConfigVersion: String by project
val bouncyCastleVersion: String by project
val logbackVersion: String by project
val junitVersion: String by project
val commonsLangVersion: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    // Logging
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // Configuration
    implementation("com.typesafe:config:$typesafeConfigVersion")

    // Server
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-resources:$ktorVersion")
    implementation("io.ktor:ktor-server-data-conversion:$ktorVersion")
    implementation("io.ktor:ktor-server-auto-head-response:$ktorVersion")
    implementation("io.ktor:ktor-server-hsts:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-compression:$ktorVersion")
    implementation("io.ktor:ktor-server-caching-headers:$ktorVersion")
    implementation("io.ktor:ktor-server-double-receive:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")

    // Crypto for safe password checking
    implementation("org.bouncycastle:bcprov-jdk15on:$bouncyCastleVersion")

    // JUnit 5 for tests definitions and running
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")

    // Asserting stuff
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")

    // Mocking
    testImplementation("io.mockk:mockk:$mockkVersion")
}