package com.sbgcore.oauth.api.customer

sealed class MatchResponse

data class MatchSuccess(val username: String) : MatchResponse()

object MatchFailure : MatchResponse()
