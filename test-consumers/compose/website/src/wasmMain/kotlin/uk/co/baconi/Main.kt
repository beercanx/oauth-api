package uk.co.baconi

import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import uk.co.baconi.session.SessionManagerEx

@OptIn(ExperimentalComposeUiApi::class)
suspend fun main() {
    CanvasBasedWindow(title = "OAuth - Wasm") {
        val sessionManager = remember { SessionManagerEx() }
        MainScreen(sessionManager)
    }
}
