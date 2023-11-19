package uk.co.baconi

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow

@OptIn(ExperimentalComposeUiApi::class)
suspend fun main() {
    CanvasBasedWindow(title = "OAuth - Wasm") {
        MainScreen()
    }
}
