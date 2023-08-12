val ktorVersion: String by project

plugins {
    jacoco
    application
    kotlin("jvm")
}

dependencies {
    api(project(":api:common"))
}

application {
    mainClass.set("uk.co.baconi.oauth.api.assets.MainKt")
}