plugins {
    jacoco
    application
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    api(project(":api:common"))
}