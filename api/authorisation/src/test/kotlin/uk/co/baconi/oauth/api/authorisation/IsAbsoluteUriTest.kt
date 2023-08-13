package uk.co.baconi.oauth.api.authorisation

import io.kotest.matchers.shouldBe
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class IsAbsoluteUriTest {

    @ParameterizedTest
    @CsvSource("https://localhost", "http://localhost", "ftp://localhost", "file:///localhost")
    fun `should return true when String is an absolute URI`(string: String) {
        string.isAbsoluteURI() shouldBe true
    }

    @ParameterizedTest
    @CsvSource("/", "/badger")
    fun `should return false when String is NOT an absolute URI`(string: String) {
        string.isAbsoluteURI() shouldBe false
    }

    @ParameterizedTest
    @CsvSource("<URI>,true", "<URI>,false")
    fun `should return default value when String is NOT a URI`(string: String, default: Boolean) {
        string.isAbsoluteURI(default) shouldBe default
    }
}