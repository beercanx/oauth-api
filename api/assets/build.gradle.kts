plugins {
    jacoco
    application
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api(project(":api:common"))

    testImplementation(enforcedPlatform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.ktor.server.call.logging)
    testImplementation(libs.ktor.client.content.negotiation)

    testImplementation(libs.kotest.assertions)
    testImplementation(libs.mockk)
}

application {
    mainClass.set("uk.co.baconi.oauth.api.assets.MainKt")
}

// TODO - Register task to replace copy that "generates resources" instead.
// Crudely copies all react bundles from user-interface into src/main/resources
tasks.register<Sync>("generateReactAssets") {
    project(":user-interface").subprojects {
        from(tasks.named("renameBundle"))
    }
    into("src/main/resources/static/js")
    rename("""(.+)\.[^.]+\.js""", "$1.js")
}
tasks.named("processResources") {
    dependsOn(tasks.named("generateReactAssets"))
}
