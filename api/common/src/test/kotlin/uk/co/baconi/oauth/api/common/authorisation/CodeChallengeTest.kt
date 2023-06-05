package uk.co.baconi.oauth.api.common.authorisation

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.Test

class CodeChallengeTest {
    @Test
    fun `should not include the value in the toString result`() {
        assertSoftly(CodeChallenge("aardvark").toString()) {
            shouldNotContain("aardvark")
            shouldContain("value='REDACTED'")
        }
    }
}