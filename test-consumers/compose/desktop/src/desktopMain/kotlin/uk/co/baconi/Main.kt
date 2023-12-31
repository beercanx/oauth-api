package uk.co.baconi

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.window.singleWindowApplication
import kotlinx.coroutines.delay
import uk.co.baconi.session.SessionManagerEx

fun main() = singleWindowApplication(title = "OAuth - Compose") {

    val sessionManager = remember { SessionManagerEx() }

    MainScreen(sessionManager)

    LaunchedEffect(Unit) {
        delay(250)
        sessionManager.startLogin()
    }
}