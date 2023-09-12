package uk.co.baconi

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import uk.co.baconi.MainScreen

@OptIn(ExperimentalComposeUiApi::class)
suspend fun main() {
    CanvasBasedWindow(title = "OAuth - Wasm") {
        MainScreen()
    }
}
