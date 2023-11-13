plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")
    id("io.gatling.gradle") version "3.9.5.6"
}

dependencies {
    // Security patching
    gatlingImplementation(platform("io.netty:netty-bom:4.1.101.Final")) {
        because("io.gatling:gatling-http brings in 4.1.92.Final")
    }
}