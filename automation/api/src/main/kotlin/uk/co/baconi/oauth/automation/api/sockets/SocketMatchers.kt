package uk.co.baconi.oauth.automation.api.sockets

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import java.net.Socket

fun beClosed() = Matcher<Socket> { value ->
    MatcherResult(
        value.isClosed,
        { "socket was not closed" },
        { "socket should not have been closed" }
    )
}

fun beConnected() = Matcher<Socket> { value ->
    MatcherResult(
        value.isConnected,
        { "socket was not connected" },
        { "socket should not have been connected" }
    )
}

fun beBound() = Matcher<Socket> { value ->
    MatcherResult(
        value.isBound,
        { "socket was not bound" },
        { "socket should not have been bound" }
    )
}