package uk.co.baconi.oauth.api.common.client

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import uk.co.baconi.oauth.api.common.client.ClientAction.Companion.fromValue


class ClientActionTest {

    @Nested
    inner class FromValue {

        @Test
        fun `should throw exception when type is invalid`() {
            assertSoftly(assertThrows<IllegalStateException> { fromValue("aardvark") }) {
                it shouldHaveMessage "No such ClientAction with value [aardvark]"
            }
        }

        @ParameterizedTest
        @EnumSource(ClientAction::class)
        fun `should return type when valid`(type: ClientAction) {
            fromValue(type.value) shouldBe type
        }
    }

}