import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    application
    kotlin("jvm") version "1.3.60"
}

repositories {
    mavenCentral()
}

dependencies {
    // Use the Kotlin JDK 8 standard library
    implementation(kotlin("stdlib-jdk8"))

    // Kotlin Test
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.4.2")
}

application {
    mainClassName = "com.sbgcore.examples.gradle.ApplicationKt"
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
