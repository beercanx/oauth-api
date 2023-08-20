plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")
    id("io.gatling.gradle") version "3.9.5.5"
}

dependencies {
    // Security patching
    constraints {
        gatlingImplementation("org.scala-lang:scala-library:2.13.11") {
            because("Dependabot thinks we have 2.13.6, but I can't see where locally")
        }
        gatlingImplementation("io.netty:netty-handler:4.1.96.Final") {
            because("gatling-http-client brings in 4.1.92.Final")
        }
    }
}