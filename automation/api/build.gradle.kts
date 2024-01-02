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
    implementation(libs.kotest.assertions)

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
        implementation("commons-codec:commons-codec:1.16.0") {
            because("Apache Http Client brings in 1.11")
        }
        implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0") {
            because("json-schema-validator brings in 2.11")
        }
        implementation("org.mozilla:rhino:1.7.14") {
            because("json-schema-validator brings in 1.7.7.2")
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