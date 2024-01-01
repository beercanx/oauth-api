package uk.co.baconi.session

import kotlin.random.Random

private val range4 = (1..4)
private val range8 = (1..8)
private val range12 = (1..12)
private val charPool = ('a'..'f') + ('0'..'9')

private fun randomChar(): Char = Random.nextInt(0, charPool.size).let { charPool[it] }

/**
 * Create a crude UUID like value
 */
actual fun generateUUID(): String = buildString {
    range8.forEach { _ -> append(randomChar()) }
    append('-')
    range4.forEach { _ -> append(randomChar()) }
    append('-')
    range4.forEach { _ -> append(randomChar()) }
    append('-')
    range4.forEach { _ -> append(randomChar()) }
    append('-')
    range12.forEach { _ -> append(randomChar()) }
}
