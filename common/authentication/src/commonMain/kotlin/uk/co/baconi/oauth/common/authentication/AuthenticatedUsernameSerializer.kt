package uk.co.baconi.oauth.common.authentication

import kotlinx.serialization.KSerializer

expect object AuthenticatedUsernameSerializer : KSerializer<AuthenticatedUsername>
