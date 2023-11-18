package uk.co.baconi.session

import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.Desktop

actual suspend fun launchAuthorise(url: Url) {
    withContext(Dispatchers.IO) {
        Desktop.getDesktop().browse(url.toURI())
    }
}