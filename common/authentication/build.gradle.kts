val ktorVersion: String by project
val mockkVersion: String by project
val junitVersion: String by project
val exposedVersion: String by project
val argon2Version: String by project

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {

    // A JVM backend to support a Javascript proxy client
    jvm()

    // A Javascript proxy client to a JVM backend
    js {
        browser() // TODO - Do we switch to a NodeJS backend rather than UI directly to authentication server?
    }

    sourceSets {

        val commonMain by getting {
            dependencies {
                // Serialisation
                api("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))

                // TODO - Better / different assertions?
                // Asserting stuff
                // testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")

                implementation("io.mockk:mockk-common:$mockkVersion")
            }
        }

        val jvmMain by getting {
            dependencies {
                // Crypto for safe password checking
                api("de.mkammerer:argon2-jvm:$argon2Version")

                api("org.jetbrains.exposed:exposed-core:$exposedVersion")
                api("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
                api("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
            }
        }
        val jvmTest by getting {
            dependencies {
                // TODO?
                // JUnit 5 for tests definitions and running
                // testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
            }
        }

        val jsMain by getting {
            dependencies {
                // HTTP Client
                implementation("io.ktor:ktor-client-js:$ktorVersion")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
            }
        }
        val jsTest by getting {
            dependencies {
                // TODO?
            }
        }
    }
}