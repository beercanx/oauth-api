import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion: String by project

plugins {
    idea
    application
    kotlin("jvm") version "1.3.61"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.3.61"
}

repositories {
    mavenCentral()
    maven { url = uri("https://kotlin.bintray.com/kotlinx") }
}

dependencies {
    // Use the Kotlin JDK 8 standard library
    implementation(kotlin("stdlib-jdk8"))

    // Logging
    implementation("ch.qos.logback:logback-classic:1.2.1")

    // Ktor server layer
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")

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
