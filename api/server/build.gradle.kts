plugins {
    jacoco
    application
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":api:common"))
    implementation(project(":api:authorisation"))
    implementation(project(":api:token"))
    implementation(project(":api:token-revocation"))
    implementation(project(":api:user-info"))
    implementation(project(":api:well-known"))

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
}