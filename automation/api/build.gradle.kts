val ktorVersion: String by project
val kotestVersion: String by project
val typesafeConfigVersion: String by project
val logbackVersion: String by project
val junitVersion: String by project
val restAssured: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {

    // Logging
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.slf4j:jcl-over-slf4j:2.0.7")
    implementation("org.slf4j:log4j-over-slf4j:2.0.9")
    implementation("org.slf4j:jul-to-slf4j:2.0.7")

    // Configuration
    implementation("com.typesafe:config:$typesafeConfigVersion")

    // Asserting stuff
    implementation("io.kotest:kotest-assertions-core:$kotestVersion")

    // Rest stuff
    implementation("io.rest-assured:rest-assured:$restAssured")
    implementation("io.rest-assured:kotlin-extensions:$restAssured")
    implementation("io.rest-assured:json-path:$restAssured")
    implementation("io.rest-assured:json-schema-validator:$restAssured")

    // JUnit 5 bits for providing things like custom resolvers
    implementation(platform("org.junit:junit-bom:$junitVersion"))
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
        implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2") {
            because("json-schema-validator brings in 2.11")
        }
        implementation("org.mozilla:rhino:1.7.12") {
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