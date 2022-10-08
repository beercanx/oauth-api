val ktorVersion: String by project

plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    api(project(":api:common"))

    api("io.ktor:ktor-server-html-builder:$ktorVersion")
}

application {
    mainClass.set("uk.co.baconi.oauth.api.authentication.MainKt")
}