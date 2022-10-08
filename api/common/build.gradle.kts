val ktorVersion: String by project
val mockkVersion: String by project
val kotestVersion: String by project
val datetimeVersion: String by project

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {

    jvm {
        compilations.all {
            kotlinOptions {
                // TODO - Can this be included in the root all project configuration or does it need setting in JVM contexts only?
                jvmTarget = "11"
            }
        }
    }

    linuxX64()

    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))

                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-server-resources:$ktorVersion")
                implementation("io.ktor:ktor-server-data-conversion:$ktorVersion")
                implementation("io.ktor:ktor-server-auto-head-response:$ktorVersion")
                implementation("io.ktor:ktor-server-hsts:$ktorVersion")
                implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                //implementation("io.ktor:ktor-server-compression:$ktorVersion") // TODO Only supported on JVM?
                implementation("io.ktor:ktor-server-caching-headers:$ktorVersion")
                implementation("io.ktor:ktor-server-double-receive:$ktorVersion")
                implementation("io.ktor:ktor-server-auth:$ktorVersion")

                // DateTime provider
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$datetimeVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))

                // TODO - Replace with Kotlin.Test assertions?
                //implementation("io.kotest:kotest-assertions-core:$kotestVersion")

                implementation("io.mockk:mockk-common:$mockkVersion")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))

                // Crypto for safe password checking
                implementation("org.bouncycastle:bcprov-jdk15on:1.70")
            }
        }
        val jvmTest by getting {
            dependencies {
                // TODO - Replace with kotlin.test runner?
                //implementation("io.kotest:kotest-runner-junit5:$kotestVersion")
            }
        }

        val linuxX64Main by getting {}
    }
}

tasks.named<Test>("jvmTest") {
    useJUnitPlatform() // TODO - Can this be included in the root all project configuration or does it need setting in JVM contexts only?
}