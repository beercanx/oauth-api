package com.sbgcore.oauth.api.customer

import java.time.OffsetDateTime

sealed class MatchResponse

data class MatchSuccess(
    val customerId: Long,
    val username: String,
    val temporary: Boolean,
    val lastLogin: OffsetDateTime
) : MatchResponse()

data class MatchFailure(val reason: MatchFailureReason) : MatchResponse()

/**
 * TODO - Taking openbet out of the equation, should we? what should we?
 */
enum class MatchFailureReason {

    Mismatch,

    // Should only be used when we have confirmed credentials
    Locked,

    // TODO - Do we care, should we, is this just here because of the openbet solution?
    @Deprecated("Use Mismatch", ReplaceWith("Mismatch"))
    Conflict,

    // TODO - Do we care, should we, is this just here because of the openbet solution?
    @Deprecated("Use Mismatch", ReplaceWith("Mismatch"))
    Other
}