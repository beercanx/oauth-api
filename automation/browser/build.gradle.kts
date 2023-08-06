val kotestVersion: String by project
val typesafeConfigVersion: String by project
val logbackVersion: String by project
val junitVersion: String by project
val seleniumJupiterVersion: String by project
val webDriverVersion: String by project
val selenideVersion: String by project

plugins {
    kotlin("jvm")
}

dependencies {

    // Logging
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // Configuration
    implementation("com.typesafe:config:$typesafeConfigVersion")

    // Asserting stuff
    implementation("io.kotest:kotest-assertions-core:$kotestVersion")

    // Browser/Selenium stuff
    testImplementation("com.codeborne:selenide:$selenideVersion")
    testImplementation("org.seleniumhq.selenium:selenium-java:$webDriverVersion")
    testImplementation("io.github.bonigarcia:selenium-jupiter:$seleniumJupiterVersion")

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