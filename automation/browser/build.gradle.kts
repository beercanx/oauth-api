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
    implementation(libs.kotest.assertions.core)

    // JUnit 5 for tests definitions and running
    testImplementation(enforcedPlatform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Security patching
    constraints {
        implementation("org.apache.httpcomponents.client5:httpclient5:5.6.1") {
            // Needs Selenide to upgrade to its Apache Http Client 5.6 to 5.6.1 or greater
            because("""
                |com.codeborne:selenide 7.16.0 brings in
                |com.codeborne:selenide-core 7.16.0 that brings in
                |org.apache.httpcomponents.client5:httpclient5 5.6
            """.trimMargin())
        }
    }
}

tasks.withType<Test>().configureEach {

    val enabled = (environment["ENABLE_AUTOMATION_BROWSER"] as String?).toBoolean()
    val headless = (environment["ENABLE_HEADLESS_BROWSER"] as String?).toBoolean()

    if(enabled) outputs.upToDateWhen { false }
    if(headless) systemProperties["selenide.headless"] = true

    useJUnitPlatform {
        if(!enabled) excludeTags("automation")
    }
}
