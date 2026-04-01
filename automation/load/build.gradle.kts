plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.allopen)
    id("io.gatling.gradle") version "3.15.0.1"
}

buildscript {
    dependencies {
        // Review on changes to `gatling-gradle-plugin`, see ../../build.gradle.kts
        for (securityBom in gradle.extra["securityBoms"] as List<*>) {
            classpath(platform(securityBom!!))
        }
        constraints {
            for (securityPatch in gradle.extra["securityPatches"] as List<*>) {
                classpath(securityPatch!!)
            }
        }
    }
}
