plugins {
    jacoco
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":api:common"))

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
}