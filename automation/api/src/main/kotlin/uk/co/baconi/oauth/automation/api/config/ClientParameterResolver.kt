package uk.co.baconi.oauth.automation.api.config

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver
import java.net.URI

// TODO - Consider replacing with a parameterised test source or find a way to support both.
class ClientParameterResolver : TypeBasedParameterResolver<Client>() {
    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Client {
        // TODO - Check for required Client state (like can introspect)
        // TODO - Pull from configuration
        return Client(
            id = ClientId("consumer-z"),
            secret = ClientSecret("7XLlyzjRpvICEkNrsgtOuuj1S30Bj9Xu"),
            redirectUri = URI.create("https://consumer-z.baconi.co.uk/callback")
        )
    }
}