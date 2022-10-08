rootProject.name = "oauth"

// API
include(":api:common")
include(":api:authorisation")
include(":api:authentication")
include(":api:session-info")
include(":api:token")
include(":api:token-introspection")
include(":api:token-revocation")
include(":api:user-info")
include(":api:well-known")
include(":api:server")

// User Interface - Not imported here as they are NPM projects.

// TODO - Automation

// TODO - Test Consumers

// Here, because workaround https://github.com/gradle/gradle/issues/1697#issuecomment-655682357
pluginManagement {
    val kotlinVersion: String by settings
    plugins {
        kotlin("jvm") version kotlinVersion apply false
        kotlin("plugin.serialization") version kotlinVersion apply false
    }
}