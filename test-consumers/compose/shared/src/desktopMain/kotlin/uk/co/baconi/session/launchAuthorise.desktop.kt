package uk.co.baconi.session

import io.ktor.http.*
import kotlinx.coroutines.withContext
import uk.co.baconi.coroutines.getIoDispatcher
import java.awt.Desktop

actual suspend fun launchAuthorise(url: Url) {
    withContext(getIoDispatcher()) {
        Desktop.getDesktop().browse(url.toURI())
    }
}