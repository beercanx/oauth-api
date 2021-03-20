package com.sbgcore.oauth.api.customer

interface MatchService {
    /**
     * Check if the provided username and password matches what we have stored.
     *
     * Also includes a check on the state of the account, such as
     */
    suspend fun match(username: String, password: String): MatchResponse
}