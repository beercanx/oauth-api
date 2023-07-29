val ktorVersion: String by project
val junitVersion: String by project
val mockkVersion: String by project
val kotestVersion: String by project

plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    api(project(":api:common"))

    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")

    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
}

application {
    mainClass.set("uk.co.baconi.oauth.api.token.MainKt")
}