plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {

    // Logging
    implementation(libs.logback.classic)
    implementation(libs.slf4j.jcl.over.slf4j)
    implementation(libs.slf4j.log4j.over.slf4j)
    implementation(libs.slf4j.jul.to.slf4j)

    // Configuration
    implementation(libs.typesafe.config)

    // Asserting stuff
    implementation(libs.kotest.assertions.core)

    // Rest stuff
    implementation(enforcedPlatform(libs.rest.assured.bom))
    implementation("io.rest-assured:rest-assured")
    implementation("io.rest-assured:kotlin-extensions")
    implementation("io.rest-assured:json-path")
    implementation("io.rest-assured:json-schema-validator")

    // JUnit 5 bits for providing things like custom resolvers
    implementation(enforcedPlatform(libs.junit.bom))
    implementation("org.junit.jupiter:junit-jupiter-api")
    implementation("org.junit.jupiter:junit-jupiter-params")

    // JUnit 5 for tests definitions and running
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Security patching
    constraints {
        implementation("commons-codec:commons-codec:1.16.1") {
            // Needs Rest Assured to upgrade to Apache Http Client 5+
            because("""
                |Rest Assured 5.4 brings in
                |Apache Http Client 4.5 (nearing EOL) that brings in
                |Commons Codec 1.11
            """.trimMargin())
        }
        implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0") {
            // Needs Rest Assured to replace its JSON schema validation library with a maintained one.
            because("""
                |Rest Assured 5.4 brings in
                |Json Schema Validator 2.2.14 (No releases since 2020) that brings in
                |Json Schema Core 1.2.14 that brings in
                |Jackson Databind 2.11.0
            """.trimMargin())
        }
        implementation("org.mozilla:rhino:1.7.15") {
            // Needs Rest Assured to replace its JSON schema validation library with a maintained one.
            because("""
                |Rest Assured 5.4 brings in
                |Json Schema Validator 2.2.14 (No releases since 2020) that brings in
                |Json Schema Core 1.2.14 that brings in
                |Rhino 1.7.7.2
            """.trimMargin())
        }
    }
}

tasks.withType<Test>().configureEach {

    val enabled = (environment["ENABLE_AUTOMATION_API"] as String?).toBoolean()
    val tls = (environment["ENABLE_AUTOMATION_API_TLS"] as String?).toBoolean()

    if(enabled) outputs.upToDateWhen { false }

    useJUnitPlatform {
        if(!enabled) excludeTags("automation")
        if(!tls) excludeTags("tls")
    }
}