package uk.co.baconi.oauth.automation.api.config

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver

class UserResolver : TypeBasedParameterResolver<User>() {
    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): User {
        // TODO - Check for required Customer state (like expected to be locked)
        // TODO - Pull from configuration
        return User("aardvark", "121212")
    }
}