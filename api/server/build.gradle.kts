plugins {
    jacoco
    application
    kotlin("jvm")
}

dependencies {
    implementation(project(":api:common"))
    implementation(project(":api:assets"))
    implementation(project(":api:authorisation"))
    implementation(project(":api:authentication"))
    implementation(project(":api:session-info"))
    implementation(project(":api:token"))
    implementation(project(":api:token-introspection"))
    implementation(project(":api:token-revocation"))
}

application {
    mainClass.set("uk.co.baconi.oauth.api.server.MainKt")
}