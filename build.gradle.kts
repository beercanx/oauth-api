import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    base
    kotlin("multiplatform") apply false // Here to enable allProjects configuration
}

allprojects {
    group = "uk.co.baconi.oauth"
    version = "0.1"
    repositories {
        mavenCentral()
        mavenLocal()
    }
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            verbose = true
            apiVersion = "1.7"
            languageVersion = "1.7"
        }
    }
    tasks.withType<Test>().configureEach {
        testLogging {
            showExceptions = true
            showStandardStreams = true
            events = setOf(
                org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
                org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
            )
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }
}

tasks.withType<Wrapper>().configureEach {
    distributionType = Wrapper.DistributionType.ALL
}