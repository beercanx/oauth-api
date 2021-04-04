package com.sbgcore.oauth.api.customer

interface MatchService {

    /**
     * Check if the provided username and password matches what we have stored.
     */
    suspend fun match(username: String, password: String): MatchResponse
}