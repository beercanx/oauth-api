val ktorVersion: String by project
val mockkVersion: String by project
val junitVersion: String by project

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {

    // TODO - Just shared models or provide database / proxy implementations depending on destination architecture?

    // TODO - Database or Proxy?
    jvm()

    // TODO - Proxy rather than just models?
    js {
        browser()
    }

    sourceSets {

        val commonMain by getting {
            dependencies {
                // TODO?
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))

                // TODO - Better / different assertions?

                implementation("io.mockk:mockk-common:$mockkVersion")
            }
        }

        val jvmMain by getting {
            dependencies {
                // Crypto for safe password checking
                implementation("org.bouncycastle:bcprov-jdk15on:1.70")
            }
        }
        val jvmTest by getting {
            dependencies {
                // TODO?
            }
        }

        val jsMain by getting {
            dependencies {
                // TODO?
            }
        }
        val jsTest by getting {
            dependencies {
                // TODO?
            }
        }
    }
}

//dependencies {
    // Serialisation
    // api("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // JUnit 5 for tests definitions and running
    // testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")

    // Asserting stuff
    // testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")

    // Mocking
    // testImplementation("io.mockk:mockk:$mockkVersion")
//}