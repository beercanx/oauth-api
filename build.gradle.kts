import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    base
    kotlin("jvm") apply false // Here to enable allProjects configuration
}

group = "uk.co.baconi.oauth"

allprojects {
    version = "0.1"
    repositories {
        mavenCentral()
        mavenLocal()
    }
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            verbose = true
            jvmTarget = "17"
            apiVersion = "1.7"
            languageVersion = "1.7"
        }
    }
    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        testLogging {
            showExceptions = true
            showStandardStreams = true
            events = setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.PASSED)
            exceptionFormat = TestExceptionFormat.FULL
        }
    }
}

tasks.withType<Wrapper>().configureEach {
    distributionType = Wrapper.DistributionType.ALL
}