package uk.co.baconi.oauth.api.common.authorisation

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class CodeChallengeMethodTest {

    @Nested
    inner class FromNameOrNull {

        @Test
        fun `should return null when type is invalid`() {
            CodeChallengeMethod.fromNameOrNull("aardvark").shouldBeNull()
        }

        @ParameterizedTest
        @EnumSource(CodeChallengeMethod::class)
        fun `should return type when valid`(type: CodeChallengeMethod) {
            CodeChallengeMethod.fromNameOrNull(type.name) shouldBe type
        }
    }
}
