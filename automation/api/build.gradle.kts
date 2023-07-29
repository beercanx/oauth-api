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
    implementation("commons-codec:commons-codec:1.15") // Patching transitive from rest-assured
    implementation("io.rest-assured:kotlin-extensions:$restAssured")
    implementation("io.rest-assured:json-path:$restAssured")
    implementation("io.rest-assured:json-schema-validator:$restAssured")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0") // Patching transitive from json-schema-validator
    implementation("com.google.guava:guava:31.1-jre") // Patching transitive from json-schema-validator

    // JUnit 5 for tests definitions and running
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// TODO - Add in a means of running the automation test pack against an environment.

tasks.withType<Test>().configureEach {
    useJUnitPlatform {
        excludeTags("automation") // Exclude automation tests from CI building. TODO - Consider support testing a local instance.
    }
}