package uk.co.baconi.oauth.api.common.authorisation

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationResponseType.Companion.fromValue
import uk.co.baconi.oauth.api.common.authorisation.AuthorisationResponseType.Companion.fromValueOrNull

class AuthorisationResponseTypeTest {

    @Nested
    inner class FromValue {

        @Test
        fun `should throw exception when type is invalid`() {
            assertSoftly(assertThrows<IllegalStateException> { fromValue("aardvark") }) {
                it shouldHaveMessage "No such AuthorisationResponseType [aardvark]"
            }
        }

        @ParameterizedTest
        @EnumSource(AuthorisationResponseType::class)
        fun `should return type when valid`(type: AuthorisationResponseType) {
            fromValue(type.value) shouldBe type
        }
    }

    @Nested
    inner class FromValueOrNull {

        @Test
        fun `should return null when type is invalid`() {
            fromValueOrNull("aardvark") should beNull()
        }

        @ParameterizedTest
        @EnumSource(AuthorisationResponseType::class)
        fun `should return type when valid`(type: AuthorisationResponseType) {
            fromValueOrNull(type.value) shouldBe type
        }
    }
}