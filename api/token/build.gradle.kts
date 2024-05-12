plugins {
    jacoco
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    api(project(":api:common"))

    testImplementation(enforcedPlatform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.ktor.server.call.logging)
    testImplementation(libs.ktor.client.content.negotiation)

    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.mockk)
}

application {
    mainClass.set("uk.co.baconi.oauth.api.token.MainKt")
}