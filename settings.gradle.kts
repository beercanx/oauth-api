rootProject.name = "oauth"

// API
include(":api:common")
include(":api:assets")
include(":api:authorisation")
include(":api:authentication")
include(":api:session-info")
include(":api:token")
include(":api:token-introspection")
include(":api:token-revocation")
include(":api:server")

// User Interface - Not imported here as they are NPM projects.

// Automation
include(":automation:api")
include(":automation:browser")
include(":automation:load")

// TODO - Test Consumers
