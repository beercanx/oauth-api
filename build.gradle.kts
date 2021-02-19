import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion: String by project
val arrowVersion: String by project

plugins {
    idea
    application
    kotlin("jvm") version "1.3.70"
    kotlin("kapt") version "1.3.70"
    kotlin("plugin.serialization") version "1.3.70"
}

repositories {
    mavenCentral()
    maven("https://kotlin.bintray.com/ktor")
    maven("https://kotlin.bintray.com/kotlinx")
}

dependencies {
    // Use the Kotlin JDK 8 standard library
    implementation(kotlin("stdlib-jdk8"))

    // Logging
    implementation("ch.qos.logback:logback-classic:1.2.1")

    // Ktor server layer
    implementation("io.ktor:ktor-server-netty:$ktorVersion")

    // Ktor content negotiation
    implementation("io.ktor:ktor-serialization:$ktorVersion")

    // Ktor typed routes
    implementation("io.ktor:ktor-locations:$ktorVersion")

    // Ktor metrics
    implementation("io.ktor:ktor-metrics:$ktorVersion")

    // Ktor sessions
    implementation("io.ktor:ktor-server-sessions:$ktorVersion")

    // Ktor HTTP client
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization-jvm:$ktorVersion")

    // Arrow Core + Arrow FX
    implementation("io.arrow-kt:arrow-fx:$arrowVersion")
    implementation("io.arrow-kt:arrow-syntax:$arrowVersion")
    kapt("io.arrow-kt:arrow-meta:$arrowVersion")

    // Nitrate - NoSQL DB
    implementation("org.dizitart:nitrite:3.4.3") // https://www.dizitart.org/nitrite-database
    implementation("org.dizitart:potassium-nitrite:3.4.3") // https://www.dizitart.org/potassium-nitrite.html

    // Kotlin Test
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.4.2")

    // Mocking
    testImplementation("io.mockk:mockk:1.9.3")

    // Ktor server test kit
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")

    // Ktor client test kit
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
    testImplementation("io.ktor:ktor-client-mock-jvm:$ktorVersion")
}

application {
    mainClassName = "com.sbgcore.oauth.api.MainKt"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = TestExceptionFormat.FULL
    }
}
