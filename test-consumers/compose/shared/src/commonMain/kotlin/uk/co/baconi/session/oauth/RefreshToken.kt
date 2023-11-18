package uk.co.baconi.session.oauth

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class RefreshToken(val value: String)