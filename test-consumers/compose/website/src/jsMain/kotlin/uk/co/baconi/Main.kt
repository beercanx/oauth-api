package uk.co.baconi

import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import uk.co.baconi.session.SessionManagerEx

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow(title = "OAuth - JS") {
        val sessionManager = remember { SessionManagerEx() }
        MainScreen(sessionManager)
    }
}
