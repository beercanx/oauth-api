import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

plugins {
    base
    kotlin("jvm") apply false // Here to enable allProjects configuration
}

group = "uk.co.baconi.oauth"

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
    }
    plugins.withId("org.jetbrains.kotlin.jvm") {
        // Replacement for kotlin { jvmToolchain(17) } in each project
        extensions.configure<KotlinJvmProjectExtension> {
            jvmToolchain(17)
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
        // Make sure the JaCoCo report is always generated after tests run.
        finalizedBy(tasks.withType<JacocoReport>())
    }
    tasks.withType<JacocoReport>().configureEach {
        // Make sure the tests are always run before generating the report.
        dependsOn(tasks.withType<Test>())
        reports {
            html.required.set(true)
            xml.required.set(true)
        }
    }
    tasks.withType<Jar>().configureEach {
        duplicatesStrategy = DuplicatesStrategy.WARN
    }
}

tasks.withType<Wrapper>().configureEach {
    distributionType = Wrapper.DistributionType.ALL
}