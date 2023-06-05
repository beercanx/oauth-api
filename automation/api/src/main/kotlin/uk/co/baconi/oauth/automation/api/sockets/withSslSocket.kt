package uk.co.baconi.oauth.automation.api.sockets

import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket

fun withSslSocket(protocol: String, hostname: String, port: Int, block: (SSLSocket) -> Unit) {
    val sslContext = SSLContext.getInstance(protocol)
    sslContext.init(null, null, null)
    val sslSocketFactory = sslContext.socketFactory
    val sslSocket = sslSocketFactory.createSocket(hostname, port) as SSLSocket
    sslSocket.use(block)
    sslSocket.close()
}