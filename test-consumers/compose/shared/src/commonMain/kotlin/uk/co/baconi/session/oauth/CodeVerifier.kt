package uk.co.baconi.session.oauth

import kotlin.jvm.JvmInline

@JvmInline
value class CodeVerifier(val value: String)
