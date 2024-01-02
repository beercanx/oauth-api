plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {

    // Logging
    implementation(libs.logback.classic)

    // Configuration
    implementation(libs.typesafe.config)

    // Browser stuff
    implementation(libs.selenide)

    // Manually asserting stuff
    implementation(libs.kotest.assertions)

    // JUnit 5 for tests definitions and running
    testImplementation(enforcedPlatform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Security patching
    constraints {
        implementation("com.google.guava:guava:32.1.3-jre") {
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