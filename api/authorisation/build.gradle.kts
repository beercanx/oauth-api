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
    mainClass.set("uk.co.baconi.oauth.api.authorisation.MainKt")
}

// Copies all react bundles from user-interface into build/generated-bundles
tasks.register<Sync>("generateReactAssets") {
    val destination = layout.buildDirectory
    project(":user-interface").subprojects {
        from(tasks.named("npmBuild"))
        into(destination.dir("generated-bundles/static/${project.name}"))
        include("*.html")
    }
}

tasks.named("processResources") {
    dependsOn(tasks.named("generateReactAssets"))
}

sourceSets {
    main {
        resources {
            srcDir(layout.buildDirectory.dir("generated-bundles"))
        }
    }
}
