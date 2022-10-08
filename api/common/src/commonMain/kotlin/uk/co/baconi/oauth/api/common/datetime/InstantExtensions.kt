package uk.co.baconi.oauth.api.common.datetime

import kotlinx.datetime.*
import kotlinx.datetime.TimeZone.Companion.UTC

fun Instant.isAfter(other: LocalDateTime): Boolean = toEpochMilliseconds() > other.toInstant(UTC).toEpochMilliseconds()
fun Instant.isBefore(other: LocalDateTime): Boolean = toEpochMilliseconds() < other.toInstant(UTC).toEpochMilliseconds()