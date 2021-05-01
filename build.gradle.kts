import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion: String by project

plugins {
    idea
    application
    kotlin("jvm") version "1.4.32"
    kotlin("plugin.serialization") version "1.4.32"
    id("io.gatling.gradle") version "3.5.1"
}

repositories {
    mavenCentral()
    maven("https://kotlin.bintray.com/ktor")
    maven("https://kotlin.bintray.com/kotlinx")
}

dependencies {
    // Use the Kotlin JDK 8 standard library
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

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
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization:$ktorVersion")

    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")

    // Kotlinx Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3-native-mt")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.4.3-native-mt")

    // Configuration
    implementation("com.typesafe:config:1.4.1")

    // Nitrate - NoSQL DB
    implementation("org.dizitart:nitrite:3.4.3") // https://www.dizitart.org/nitrite-database
    implementation("org.dizitart:potassium-nitrite:3.4.3") // https://www.dizitart.org/potassium-nitrite.html

    // Bouncy Castle - bcrypt provider for the JVM
    implementation("org.bouncycastle:bcprov-jdk15on:1.68")

    // JUnit 5 for tests definitions and running
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")

    // Kotest for assertions and matchers
    testImplementation("io.kotest:kotest-assertions-core:4.3.1")

    // Mocking
    testImplementation("io.mockk:mockk:1.9.3")

    // Ktor server test kit
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")

    // Ktor client test kit
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
    testImplementation("io.ktor:ktor-client-mock-jvm:$ktorVersion")

    // Test data generation
    testImplementation("org.apache.commons:commons-lang3:3.12.0")
}

application {
    mainClassName = "com.sbgcore.oauth.api.MainKt"
}

apply {
    from("gradle/gatling.gradle")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
        languageVersion = "1.4"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = TestExceptionFormat.FULL
    }
}
