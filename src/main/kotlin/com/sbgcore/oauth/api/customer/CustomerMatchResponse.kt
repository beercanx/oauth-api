package com.sbgcore.oauth.api.customer

sealed class CustomerMatchResponse

data class CustomerMatchSuccess(val username: String) : CustomerMatchResponse()

object CustomerMatchFailure : CustomerMatchResponse()
