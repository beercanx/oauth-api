package uk.co.baconi

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.singleWindowApplication
import kotlinx.coroutines.runBlocking
import uk.co.baconi.session.SessionService

fun main() = singleWindowApplication(title = "OAuth - Compose") {
    //LoadingScreen("Desktop")
    LaunchedEffect(Unit) {
        val sessionService = SessionService(createHttpClient())
        sessionService.getSession("aardvark", "121212")
    }
}