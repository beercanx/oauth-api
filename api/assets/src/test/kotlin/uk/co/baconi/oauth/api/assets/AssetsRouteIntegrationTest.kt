package uk.co.baconi.oauth.api.assets

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.collections.shouldExist
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.beEmpty
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.ContentType.*
import io.ktor.http.HttpStatusCode.Companion.MethodNotAllowed
import io.ktor.http.HttpStatusCode.Companion.NotFound
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.co.baconi.oauth.api.common.CommonModule.common

class AssetsRouteIntegrationTest: AssetsRoute {

    companion object {
        private const val ASSETS_LOCATION = "/assets/js"
        private const val AUTHENTICATION_JS = "authentication.js"
    }

    private fun setupApplication(block: suspend ApplicationTestBuilder.(HttpClient) -> Unit) {
        testApplication {
            application {
                common()
            }
            routing {
                assets() // underTest
            }
            block(client)
        }
    }

    @ParameterizedTest
    @CsvSource("POST", "PUT", "PATCH", "DELETE", "OPTIONS")
    fun `should only support get and head requests`(method: String) = setupApplication { client ->

        val response = client.request("$ASSETS_LOCATION/$AUTHENTICATION_JS") {
            this.method = HttpMethod.parse(method)
        }

        assertSoftly(response) {
            status shouldBe NotFound
            bodyAsText() should beEmpty()
        }
    }

    @Test
    fun `should not suggest that the content can be cached`() = setupApplication { client ->

        val response = client.get("$ASSETS_LOCATION/$AUTHENTICATION_JS")

        assertSoftly(response) {
            status shouldBe OK
            assertSoftly(cacheControl()) {
                shouldExist(headerWithValue("no-cache"))
                shouldExist(headerWithValue("no-store"))
                shouldExist(headerWithValue("max-age=0"))
                shouldExist(headerWithValue("must-revalidate"))
                shouldExist(headerWithValue("proxy-revalidate"))
            }
        }
    }

    @Test
    fun `should return javascript bundles marked as javascript`() = setupApplication { client ->

        val response = client.get("$ASSETS_LOCATION/$AUTHENTICATION_JS")

        assertSoftly(response) {
            status shouldBe OK
            contentType() shouldBe Application.JavaScript
        }
    }

    private fun headerWithValue(value: String): (HeaderValue) -> Boolean = object : (HeaderValue) -> Boolean {
        override fun toString(): String = "(headerValue.value == $value)"
        override fun invoke(headerValue: HeaderValue): Boolean = headerValue.value == value
    }
}