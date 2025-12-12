package uk.co.baconi

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.ktor.http.*
import kotlinx.browser.window
import uk.co.baconi.session.SessionManagerEx

@OptIn(ExperimentalComposeUiApi::class)
fun main() {

    ComposeViewport {

        val sessionManager = remember { SessionManagerEx() }

        MainScreen(sessionManager)

        val session = sessionManager.session.collectAsState().value
        LaunchedEffect(Unit) {
            val currentUrl = Url(window.location.href)
            when {
                session != null -> console.log("Already Logged In!")
                currentUrl.parameters.contains("code") -> sessionManager.handleCallback(currentUrl)
                !currentUrl.parameters.contains("error") -> sessionManager.startLogin()
            }
        }
    }
}
