import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    jacoco
    application
    kotlin("jvm") version "1.7.0"
    kotlin("plugin.serialization") version "1.7.0"
    id("io.gatling.gradle") version "3.5.1"
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

dependencies {
    // Use the Kotlin JDK 8 standard library
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    // Logging
    implementation("ch.qos.logback:logback-classic:1.2.11")

    // Ktor server layer
    implementation("io.ktor:ktor-server-netty:2.0.2")
    implementation("io.ktor:ktor-server-hsts:2.0.2")
    implementation("io.ktor:ktor-server-caching-headers:2.0.2")
    implementation("io.ktor:ktor-server-compression:2.0.2")
    implementation("io.ktor:ktor-server-double-receive:2.0.2")
    implementation("io.ktor:ktor-server-data-conversion:2.0.2")
    implementation("io.ktor:ktor-server-auto-head-response:2.0.2")

    // Ktor view layer
    implementation("io.ktor:ktor-server-html-builder:2.0.2")

    // Ktor content negotiation
    implementation("io.ktor:ktor-server-content-negotiation:2.0.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.2")

    // Ktor typed routes
    implementation("io.ktor:ktor-server-locations:2.0.2")
    implementation("io.ktor:ktor-server-resources:2.0.2")

    // Ktor metrics
    implementation("io.ktor:ktor-server-metrics:2.0.2")

    // Ktor sessions
    implementation("io.ktor:ktor-server-sessions:2.0.2")

    // Ktor HTTP client
    implementation("io.ktor:ktor-client-okhttp:2.0.2")
    implementation("io.ktor:ktor-client-serialization:2.0.2")

    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    // Kotlinx Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.2")

    // Configuration
    implementation("com.typesafe:config:1.4.2")

    // Nitrate - NoSQL DB
    implementation("org.dizitart:nitrite:3.4.4") // https://www.dizitart.org/nitrite-database
    implementation("org.dizitart:potassium-nitrite:3.4.4") // https://www.dizitart.org/potassium-nitrite.html
    implementation(platform("com.fasterxml.jackson:jackson-bom:2.12.7")) // patching vulnerabilities in jackson brought in by nitrite.

    // Bouncy Castle - bcrypt provider for the JVM
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")

    // JUnit 5 for tests definitions and running
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")

    // Kotest for assertions and matchers
    testImplementation("io.kotest:kotest-assertions-core:5.3.1")

    // Mocking
    testImplementation("io.mockk:mockk:1.12.4")

    // Ktor server test kit
    testImplementation("io.ktor:ktor-server-tests:2.0.2")

    // Ktor client test kit
    testImplementation("io.ktor:ktor-client-mock:2.0.2")

    // Test data generation
    testImplementation("org.apache.commons:commons-lang3:3.12.0")
}

application {
    mainClass.set("uk.co.baconi.oauth.api.MainKt")
}

apply {
    from("gradle/gatling.gradle")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11" // TODO - Upgrade to JDK 17
        languageVersion = "1.7"
    }
}

tasks.withType<JacocoReport>().configureEach {
    // Make sure the tests are always run before generating the report.
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true)
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = TestExceptionFormat.FULL
    }
    // Make sure the JaCoCo report is always generated after tests run.
    finalizedBy(tasks.jacocoTestReport)
}

tasks.withType<Wrapper>().configureEach {
    distributionType = Wrapper.DistributionType.ALL
}