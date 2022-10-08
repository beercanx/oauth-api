val ktorVersion: String by project

plugins {
    kotlin("js")
    kotlin("plugin.serialization")
}

kotlin {
    js {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
        binaries.executable() // TODO - Remove as its probably all going into the ':user-interface:server' module.
    }
}

dependencies {
    api(project(":user-interface:common"))
    api(project(":common:authentication-client"))

    api("io.ktor:ktor-client-core:$ktorVersion")
    api("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
}