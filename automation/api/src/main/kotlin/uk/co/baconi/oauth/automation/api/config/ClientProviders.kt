package uk.co.baconi.oauth.automation.api.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.AnnotationBasedArgumentsProvider
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsSource
import uk.co.baconi.oauth.automation.api.config.ClientType.Confidential
import uk.co.baconi.oauth.automation.api.config.GrantType.AuthorizationCode
import uk.co.baconi.oauth.automation.api.getConfig
import uk.co.baconi.oauth.automation.api.getEnumSetOrEmpty
import uk.co.baconi.oauth.automation.api.getUri
import java.util.stream.Stream
import kotlin.annotation.AnnotationTarget.ANNOTATION_CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.streams.asStream

@Target(ANNOTATION_CLASS, FUNCTION)
@ArgumentsSource(ClientArgumentsProvider::class)
annotation class ClientSource(

    /**
     * [Client]s will be one of these types of client.
     */
    val clientTypes: Array<ClientType> = [Confidential],

    /**
     * [Client]s must be able to perform all these grant types.
     */
    val grantTypes: Array<GrantType> = [AuthorizationCode]
)

/**
 * Used to provide multiple [Client]s for [ParameterizedTest]s based on the desired configuration.
 */
class ClientArgumentsProvider : AnnotationBasedArgumentsProvider<ClientSource>() {

    companion object {
        private val base = ConfigFactory.load().getConfig("uk.co.baconi.oauth.automation.api").getObject("clients")

        // TODO - Maybe convert to a [Client] on load, then filter?
        private val clients: Map<ClientId, Config> = base.keys.associate { key -> ClientId(key) to base.getConfig(key) }
    }

    override fun provideArguments(context: ExtensionContext, clientSource: ClientSource): Stream<out Arguments> {
        return provideClients(clientSource)
            .asStream()
            .map(Arguments::of)
    }

    fun provideClients(clientSource: ClientSource): Sequence<Client> {
        return clients
            .entries
            .asSequence()
            .filter(::isEnabled)
            .filter(hasValidClientType(clientSource))
            .filter(hasValidGrantType(clientSource))
            .map(::toClient)
    }

    private fun isEnabled(entry: Map.Entry<ClientId, Config>): Boolean = entry.value.getBoolean("enabled")

    private fun hasValidClientType(clientSource: ClientSource) = { (_, value): Map.Entry<ClientId, Config> ->
        clientSource.clientTypes.contains(value.getClientType())
    }

    private fun hasValidGrantType(clientSource: ClientSource) = { (_, value): Map.Entry<ClientId, Config> ->
        value.getGrantTypes().containsAll(clientSource.grantTypes.asList())
    }

    private fun toClient(entry: Map.Entry<ClientId, Config>): Client {

        val (clientId, value) = entry
        val type = value.getClientType()
        val grantTypes = value.getGrantTypes()

        return when (type) {
            Confidential -> object : ConfidentialClient {
                override val id = clientId
                override val grantTypes = grantTypes
                override val redirectUri by lazy { value.getUri("redirectUri") }
                override val secret by lazy { value.getClientSecret() }
                override fun toString() = "ConfidentialClient(id='${id.value}')"
            }

            ClientType.Public -> object : PublicClient {
                override val id = clientId
                override val grantTypes = grantTypes
                override val redirectUri by lazy { value.getUri("redirectUri") }
                override fun toString() = "PublicClient(id='${id.value}')"
            }
        }
    }

    private fun Config.getClientType(): ClientType = getEnum(ClientType::class.java, "type")
    private fun Config.getClientSecret(): ClientSecret = getString("secret").let(::ClientSecret)
    private fun Config.getGrantTypes(): Set<GrantType> = getEnumSetOrEmpty("grantTypes")
}

abstract class ClientResolver : ParameterResolver {

    private val clientProvider = ClientArgumentsProvider()

    abstract fun supportsParameter(context: ParameterContext): Boolean

    abstract fun getSource(source: ClientSource?): ClientSource

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {

        // Don't resolve for a [ParameterizedTest] using [ClientSource].
        if (parameterContext.declaringExecutable.isAnnotationPresent(ClientSource::class.java)) {
            return false
        }

        if (parameterContext.parameter.type != Client::class.java) {
            return false
        }

        return supportsParameter(parameterContext)
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Client {
        val source = getSource(parameterContext.findAnnotation(ClientSource::class.java).orElse(null))
        return clientProvider.provideClients(source).first()
    }
}

class ClientSourceResolver : ClientResolver() {

    override fun supportsParameter(context: ParameterContext): Boolean {
        return context.isAnnotated(ClientSource::class.java)
    }

    override fun getSource(source: ClientSource?): ClientSource {
        return checkNotNull(source) { "ClientSource should not be null" }
    }
}

class ConfidentialClientResolver : ClientResolver() {

    private val defaultClientSource = ClientSource(arrayOf(Confidential), arrayOf(AuthorizationCode))

    override fun supportsParameter(context: ParameterContext): Boolean {

        val source = context.findAnnotation(ClientSource::class.java).orElse(null)
        when {
            source is ClientSource && !source.clientTypes.contentEquals(arrayOf(Confidential)) -> return false
        }

        return context.parameter.type == ConfidentialClient::class.java
    }

    override fun getSource(source: ClientSource?): ClientSource {
        return source ?: defaultClientSource
    }
}
