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
import org.slf4j.LoggerFactory
import uk.co.baconi.oauth.automation.api.config.ClientType.Confidential
import uk.co.baconi.oauth.automation.api.config.ClientType.Public
import uk.co.baconi.oauth.automation.api.config.GrantType.AuthorizationCode
import uk.co.baconi.oauth.automation.api.getConfig
import uk.co.baconi.oauth.automation.api.getEnumSetOrEmpty
import uk.co.baconi.oauth.automation.api.getUri
import java.util.stream.Stream
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass
import kotlin.streams.asStream

@Target(ANNOTATION_CLASS, FUNCTION, VALUE_PARAMETER)
@ArgumentsSource(ClientArgumentsProvider::class)
annotation class ClientSource(

    /**
     * [Client]s will be one of these types of client.
     */
    val clientTypes: Array<ClientType> = [],

    /**
     * [Client]s must be able to perform all these grant types.
     */
    val grantTypes: Array<GrantType> = [],

    /**
     * [Client]s must have these extra capabilities.
     */
    val capabilities: Array<ClientCapabilities> = [],
)

/**
 * Used to provide multiple [Client]s for [ParameterizedTest]s based on the desired configuration.
 */
class ClientArgumentsProvider : AnnotationBasedArgumentsProvider<ClientSource>() {

    companion object {

        private val base = ConfigFactory.load().getConfig("uk.co.baconi.oauth.automation.api").getObject("clients")

        private val clients: Sequence<Client> = base.keys
            .associate { key -> ClientId(key) to base.getConfig(key) }
            .filter(::isEnabled)
            .map(::toClient)
            .asSequence()

        private fun isEnabled(entry: Map.Entry<ClientId, Config>): Boolean = entry.value.getBoolean("enabled")

        private fun toClient(entry: Map.Entry<ClientId, Config>): Client {

            val (clientId, value) = entry
            val type: ClientType = value.getEnum(ClientType::class.java, "type")
            val grantTypes = value.getEnumSetOrEmpty<GrantType>("grantTypes")
            val capabilities = value.getEnumSetOrEmpty<ClientCapabilities>("capabilities")

            return when (type) {
                Confidential -> object : ConfidentialClient {
                    override val id = clientId
                    override val grantTypes = grantTypes
                    override val capabilities = capabilities
                    override val redirectUri by lazy { value.getUri("redirectUri") }
                    override val secret by lazy { value.getString("secret").let(::ClientSecret) }
                    override fun toString() = "ConfidentialClient(id='${id.value}')"
                }

                Public -> object : PublicClient {
                    override val id = clientId
                    override val grantTypes = grantTypes
                    override val capabilities = capabilities
                    override val redirectUri by lazy { value.getUri("redirectUri") }
                    override fun toString() = "PublicClient(id='${id.value}')"
                }
            }
        }
    }

    override fun provideArguments(context: ExtensionContext, clientSource: ClientSource): Stream<out Arguments> {
        return provideClients(clientSource)
            .asStream()
            .map(Arguments::of)
    }

    fun provideClients(clientSource: ClientSource): Sequence<Client> {
        return clients
            .filter(hasValidClientType(clientSource))
            .filter(hasValidGrantType(clientSource))
            .filter(hasValidClientCapabilities(clientSource))
    }

    private fun hasValidClientType(clientSource: ClientSource): (Client) -> Boolean = { value ->
        clientSource.clientTypes.contains(value.type)
    }

    private fun hasValidGrantType(clientSource: ClientSource): (Client) -> Boolean = { value ->
        value.grantTypes.containsAll(clientSource.grantTypes.asList())
    }

    private fun hasValidClientCapabilities(clientSource: ClientSource): (Client) -> Boolean = { value ->
        value.capabilities.containsAll(clientSource.capabilities.asList())
    }
}

abstract class ClientResolver(private val kClass: KClass<*>) : ParameterResolver {

    private val clientProvider = ClientArgumentsProvider()

    abstract fun annotationCheck(context: ParameterContext): Boolean

    abstract fun getSource(context: ParameterContext): ClientSource

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {

        // Don't resolve for a [ParameterizedTest] using [ClientSource].
        if (parameterContext.declaringExecutable.isAnnotationPresent(ClientSource::class.java)) {
            return false
        }

        // Only support the defined type
        if (!kClass.java.isAssignableFrom(parameterContext.parameter.type)) {
            return false
        }

        return annotationCheck(parameterContext)
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Client {
        return clientProvider.provideClients(getSource(parameterContext)).first()
    }
}

class ClientSourceResolver : ClientResolver(Client::class) {
    override fun annotationCheck(context: ParameterContext) = context.isAnnotated(ClientSource::class.java)
    override fun getSource(context: ParameterContext) = context.findAnnotation(ClientSource::class.java).get()
}

class ConfidentialClientResolver : ClientResolver(ConfidentialClient::class) {
    private val confidentialClientSource = ClientSource(arrayOf(Confidential), arrayOf(AuthorizationCode))
    override fun annotationCheck(context: ParameterContext) = !context.isAnnotated(ClientSource::class.java)
    override fun getSource(context: ParameterContext) = confidentialClientSource
}

class PublicClientResolver : ClientResolver(PublicClient::class) {
    private val publicClientSource = ClientSource(arrayOf(Public), arrayOf(AuthorizationCode))
    override fun annotationCheck(context: ParameterContext) = !context.isAnnotated(ClientSource::class.java)
    override fun getSource(context: ParameterContext) = publicClientSource
}
