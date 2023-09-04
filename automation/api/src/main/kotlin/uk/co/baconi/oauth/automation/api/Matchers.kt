package uk.co.baconi.oauth.automation.api

import io.kotest.matchers.string.beUUID
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

/**
 * Lazy reuse of Kotest's [beUUID]
 */
fun isUUID(): TypeSafeMatcher<String> = object : TypeSafeMatcher<String>(){

    override fun matchesSafely(item: String): Boolean {
        return beUUID().test(item).passed()
    }

    override fun describeTo(description: Description) {
        description.appendText("be UUID")
    }
}