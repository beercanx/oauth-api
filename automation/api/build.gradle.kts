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

    // Configuration
    implementation("com.typesafe:config:$typesafeConfigVersion")

    // Asserting stuff
    implementation("io.kotest:kotest-assertions-core:$kotestVersion")

    // Rest stuff
    implementation("io.rest-assured:rest-assured:$restAssured")
    implementation("io.rest-assured:kotlin-extensions:$restAssured")
    implementation("io.rest-assured:json-path:$restAssured")
    implementation("io.rest-assured:json-schema-validator:$restAssured")

    // JUnit 5 for tests definitions and running
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
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

// TODO - Add in a means of running the automation test pack against an environment.

tasks.withType<Test>().configureEach {
    useJUnitPlatform {
        excludeTags("automation") // Exclude automation tests from CI building. TODO - Consider support testing a local instance.
    }
}