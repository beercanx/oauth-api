rootProject.name = "oauth"

// TODO - Common

// API
include(":api:common")
include(":api:authorisation")
include(":api:token")
include(":api:token-revocation")
include(":api:user-info")
include(":api:well-known")
include(":api:server")

// TODO - Automation

// TODO - User Interface

// TODO - Test Consumers

// Here, because workaround https://github.com/gradle/gradle/issues/1697#issuecomment-655682357
pluginManagement {
    val kotlinVersion: String by settings
    plugins {
        kotlin("jvm") version kotlinVersion apply false
        kotlin("multiplatform") version kotlinVersion apply false
        kotlin("plugin.serialization") version kotlinVersion apply false
    }
}