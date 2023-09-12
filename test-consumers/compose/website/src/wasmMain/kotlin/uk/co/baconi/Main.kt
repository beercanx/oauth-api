package uk.co.baconi

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import uk.co.baconi.session.SessionService

@OptIn(ExperimentalComposeUiApi::class)
suspend fun main() {

    CanvasBasedWindow(title = "OAuth - Wasm") {
        //LoadingScreen("WASM")
        LaunchedEffect(Unit) {
            val sessionService = SessionService(createHttpClient())
            sessionService.getSession("aardvark", "121212")
        }
    }
}
