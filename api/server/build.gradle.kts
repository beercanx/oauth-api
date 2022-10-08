plugins {
    application
    kotlin("jvm")
}

dependencies {
    implementation(project(":api:common"))
    implementation(project(":api:authorisation"))
    implementation(project(":api:token"))
    implementation(project(":api:token-introspection"))
    implementation(project(":api:token-revocation"))
    implementation(project(":api:user-info"))
    implementation(project(":api:well-known"))
}

application {
    mainClass.set("uk.co.baconi.oauth.api.server.MainKt")
}