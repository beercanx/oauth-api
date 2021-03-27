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

    // TODO - Do we care, should we, is this just here because of the openbet solution?
    Locked,

    // TODO - Do we care, should we, is this just here because of the openbet solution?
    Conflict,

    // TODO - Do we care, should we, is this just here because of the openbet solution?
    Other
}