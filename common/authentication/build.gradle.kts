val ktorVersion: String by project
val mockkVersion: String by project
val junitVersion: String by project
val exposedVersion: String by project

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {

    jvm()

    js {
        browser()
    }

    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))

                // TODO - Better / different assertions?
                // Just use kotest runner?
                // Asserting stuff
                // testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")

                implementation("io.mockk:mockk-common:$mockkVersion")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
            }
        }
        val jvmTest by getting {
            dependencies {
                // TODO?
                // testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
            }
        }

        val jsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-js:$ktorVersion")
            }
        }
        val jsTest by getting {
            dependencies {
                // TODO?
            }
        }
    }
}