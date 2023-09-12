package uk.co.baconi

import androidx.compose.ui.window.singleWindowApplication
import uk.co.baconi.MainScreen

fun main() = singleWindowApplication(title = "OAuth - Compose") {
    MainScreen()
}