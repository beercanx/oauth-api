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

// User Interface
include(":user-interface:authentication")
// TODO include(":user-interface:authorise")
// TODO include(":user-interface:confirm-details")
// TODO include(":user-interface:registration")
// TODO include(":user-interface:session-management")

// Automation
include(":automation:api")
include(":automation:browser")
include(":automation:load")

// TODO - Test Consumers
