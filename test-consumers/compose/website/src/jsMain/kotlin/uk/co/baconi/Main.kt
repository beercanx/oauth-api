package uk.co.baconi

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import io.ktor.http.*
import kotlinx.browser.window
import kotlinx.coroutines.delay
import uk.co.baconi.session.SessionManagerEx

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow(title = "OAuth - JS") {

        val sessionManager = remember { SessionManagerEx() }

        MainScreen(sessionManager)

        LaunchedEffect(Unit) {
            val currentUrl = Url(window.location.href)
            when {
                sessionManager.session.collectAsState().value == null -> console.log("Already Logged In!")
                currentUrl.parameters.contains("code") -> sessionManager.handleCallback(currentUrl)
                !currentUrl.parameters.contains("error") -> sessionManager.startLogin()
            }
        }
    }
}
