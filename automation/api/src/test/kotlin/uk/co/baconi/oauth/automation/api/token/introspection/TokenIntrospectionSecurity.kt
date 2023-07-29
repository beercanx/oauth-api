package uk.co.baconi.oauth.automation.api.token.introspection

import com.typesafe.config.ConfigFactory
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.shouldStartWith
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource
import uk.co.baconi.oauth.automation.api.*
import uk.co.baconi.oauth.automation.api.sockets.withSslSocket
import uk.co.baconi.oauth.automation.api.sockets.beBound
import uk.co.baconi.oauth.automation.api.sockets.beClosed
import uk.co.baconi.oauth.automation.api.sockets.beConnected
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLSocket

@Tag(RFC7662)
@Tag(AUTOMATION)
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
    fun `must support tls 1-2`() = withSslSocket("TLSv1.2", hostname, port) { socket ->
        socket.startHandshake()
        assertSoftly {
            socket should beBound()
            socket shouldNot beClosed()
            socket should beConnected()
        }
    }

    @Test
    fun `should support tls 1-3`() = withSslSocket("TLSv1.3", hostname, port) { socket ->
        socket.startHandshake()
        assertSoftly {
            socket should beBound()
            socket shouldNot beClosed()
            socket should beConnected()
        }
    }

    @Test
    fun `must not support tls 1-1`() = withSslSocket("TLSv1.1", hostname, port) { socket ->

        val exception = shouldThrow<SSLHandshakeException> {
            socket.startHandshake()
        }

        assertSoftly {
            exception.message shouldStartWith "No appropriate protocol"

            socket should beBound()
            socket should beClosed()
            socket should beConnected()
        }
    }

    @Test
    fun `must not support tls 1-0`() = withSslSocket("TLSv1", hostname, port) { socket ->

        val exception = shouldThrow<SSLHandshakeException> {
            socket.startHandshake()
        }

        assertSoftly {
            exception.message shouldStartWith "No appropriate protocol"

            socket should beBound()
            socket should beClosed()
            socket should beConnected()
        }
    }

    @Test
    fun `must not support ssl 3-0`() = withSslSocket("SSLv3", hostname, port) { socket ->

        val exception = shouldThrow<SSLHandshakeException> {
            socket.startHandshake()
        }

        assertSoftly {
            exception.message shouldStartWith "No appropriate protocol"

            socket should beBound()
            socket should beClosed()
            socket should beConnected()
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = ["/cipher-suites/insecure.csv"])
    fun `must not use insecure cipher suites`(cipherSuite: String) = withSslSocket("TLSv1.2", hostname, port) { socket ->

        try {
            socket.enabledCipherSuites = arrayOf(cipherSuite)
        } catch (exception: IllegalArgumentException) {
            return@withSslSocket assertSoftly {
                // TODO - Might be better reported neither as a failure or error when unsupported nor as success.
                exception.message shouldStartWith "Unsupported CipherSuite: $cipherSuite"
            }
        }

        val exception = shouldThrow<SSLHandshakeException> {
            socket.startHandshake()
        }

        assertSoftly {
            exception.message shouldStartWith "No appropriate protocol"

            socket should beBound()
            socket should beClosed()
            socket should beConnected()
        }
    }

    @ParameterizedTest
    @Execution(ExecutionMode.CONCURRENT)
    @CsvFileSource(resources = ["/cipher-suites/weak.csv"])
    fun `should not use weak cipher suites`(cipherSuite: String) = withSslSocket("TLSv1.2", hostname, port) { socket ->

        try {
            socket.enabledCipherSuites = arrayOf(cipherSuite)
        } catch (exception: IllegalArgumentException) {
            return@withSslSocket assertSoftly {
                // TODO - Might be better reported neither as a failure or error when unsupported nor as success.
                exception.message shouldStartWith "Unsupported CipherSuite: $cipherSuite"
            }
        }

        val exception = shouldThrow<SSLHandshakeException> {
            socket.startHandshake()
        }

        assertSoftly {
            exception.message shouldStartWith "No appropriate protocol"

            socket should beBound()
            socket should beClosed()
            socket should beConnected()
        }
    }
}