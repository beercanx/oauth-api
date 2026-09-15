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
    constraints {
        // TODO - Review need for constraint when Ktor bumps
        testImplementation("org.apache.httpcomponents.client5:httpclient5:5.6.4")
    }

    testImplementation(libs.ktor.server.call.logging)
    testImplementation(libs.ktor.client.content.negotiation)

    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.mockk)
}

application {
    mainClass.set("uk.co.baconi.oauth.api.token.MainKt")
}