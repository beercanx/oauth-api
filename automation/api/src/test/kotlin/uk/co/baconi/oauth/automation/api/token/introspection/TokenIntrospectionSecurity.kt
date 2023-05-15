package uk.co.baconi.oauth.automation.api.token.introspection

import com.typesafe.config.ConfigFactory
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.automation.api.*
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLSocket

@Tag(RFC7662)
@Tag(TOKEN_INTROSPECTION)
class TokenIntrospectionSecurity {

    private val config = ConfigFactory.load().getConfig("uk.co.baconi.oauth.automation.api.token.introspection")
    private val hostname = config.getString("hostname")
    private val port = config.getInt("port")

    /**
     * Since the introspection endpoint takes in OAuth 2.0 tokens as parameters and responds with information used to
     * make authorization decisions, the server MUST support Transport Layer Security (TLS) 1.2 [RFC5246] and MAY
     * support additional transport-layer mechanisms meeting its security requirements.  When using TLS, the client or
     * protected resource MUST perform a TLS/SSL server certificate check, as specified in [RFC6125].  Implementation
     * security considerations can be found in Recommendations for Secure Use of TLS and DTLS [BCP195].
     */
    @Test
    fun `must support tls 1-2`() = withSslSocket("TLSv1.2") { socket ->
        socket.startHandshake()
        socket.isBound shouldBe true
        socket.isClosed shouldBe false
        socket.isConnected shouldBe true
    }

    @Test
    fun `should support tls 1-3`() = withSslSocket("TLSv1.3") { socket ->
        socket.startHandshake()
        socket.isBound shouldBe true
        socket.isClosed shouldBe false
        socket.isConnected shouldBe true
    }

    @Test
    fun `must not support tls 1-1`() = withSslSocket("TLSv1.1") { socket ->

        val exception = shouldThrow<SSLHandshakeException> {
            socket.startHandshake()
        }

        exception.message shouldStartWith "No appropriate protocol"

        socket.isClosed shouldBe true
    }

    @Test
    fun `must not support tls 1-0`() = withSslSocket("TLSv1") { socket ->

        val exception = shouldThrow<SSLHandshakeException> {
            socket.startHandshake()
        }

        exception.message shouldStartWith "No appropriate protocol"

        socket.isClosed shouldBe true
    }

    @Test
    fun `must not support ssl 3-0`() = withSslSocket("SSLv3") { socket ->

        val exception = shouldThrow<SSLHandshakeException> {
            socket.startHandshake()
        }

        exception.message shouldStartWith "No appropriate protocol"

        socket.isClosed shouldBe true
    }

    // TODO - Include a cipher suites check???

    private fun withSslSocket(protocol: String, block: (SSLSocket) -> Unit) {
        val sslContext = SSLContext.getInstance(protocol)
        sslContext.init(null, null, null)
        val sslSocketFactory = sslContext.socketFactory
        val sslSocket = sslSocketFactory.createSocket(hostname, port) as SSLSocket
        sslSocket.use(block)
    }
}