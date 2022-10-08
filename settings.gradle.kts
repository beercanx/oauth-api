rootProject.name = "oauth"

// TODO - Common
include(":common:authentication")

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

// User Interface
include(":user-interface:common")
include(":user-interface:authentication")
include(":user-interface:confirm-details")
include(":user-interface:registrations")
include(":user-interface:session-management")
//include(":user-interface:server") // TODO - Work out the best way, maybe its just a static assets server?

// TODO - Automation

// TODO - Test Consumers

// Here, because workaround https://github.com/gradle/gradle/issues/1697#issuecomment-655682357
pluginManagement {
    val kotlinVersion: String by settings
    plugins {
        kotlin("js") version kotlinVersion apply false
        kotlin("jvm") version kotlinVersion apply false
        kotlin("multiplatform") version kotlinVersion apply false
        kotlin("plugin.serialization") version kotlinVersion apply false
    }
}