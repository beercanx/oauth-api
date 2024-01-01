package uk.co.baconi.session

actual fun generateUUID(): String = java.util.UUID.randomUUID().toString()
