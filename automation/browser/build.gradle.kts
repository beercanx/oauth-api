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
}

tasks.withType<Test>().configureEach {

    val enabled = (environment["ENABLE_AUTOMATION_BROWSER"] as String?).toBoolean()

    if(enabled) outputs.upToDateWhen { false }

    useJUnitPlatform {
        if(!enabled) excludeTags("automation")
    }
}
