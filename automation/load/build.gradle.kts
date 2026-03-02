plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.allopen)
    id("io.gatling.gradle") version "3.14.9.5"
}

buildscript {
    dependencies {
        // Review on changes to `gatling-gradle-plugin`, see ../../build.gradle.kts
        for (securityBom in gradle.extra["securityBoms"] as List<*>) {
            classpath(platform(securityBom!!))
        }
    }
}