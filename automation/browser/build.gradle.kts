val kotestVersion: String by project
val typesafeConfigVersion: String by project
val logbackVersion: String by project
val junitVersion: String by project
val selenideVersion: String by project

plugins {
    kotlin("jvm")
}

dependencies {

    // Logging
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // Configuration
    implementation("com.typesafe:config:$typesafeConfigVersion")

    // Browser stuff
    implementation("com.codeborne:selenide:$selenideVersion")

    // Manually asserting stuff
    implementation("io.kotest:kotest-assertions-core:$kotestVersion")

    // JUnit 5 for tests definitions and running
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Security patching
    constraints {
        implementation("com.google.guava:guava:32.1.2-jre") {
            because("selenide brings in 31.1-jre")
        }
        implementation("io.netty:netty-handler:4.1.101.Final") {
            because("selenide brings in 4.1.92.Final")
        }
    }
}

tasks.withType<Test>().configureEach {

    val enabled = (environment["ENABLE_AUTOMATION_BROWSER"] as String?).toBoolean()

    if(enabled) outputs.upToDateWhen { false }

    useJUnitPlatform {
        if(!enabled) excludeTags("automation")
    }
}