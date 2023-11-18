package uk.co.baconi

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import uk.co.baconi.loading.LoadingScreen
import uk.co.baconi.session.Session
import uk.co.baconi.session.SessionManager
import uk.co.baconi.session.SessionScreen
import uk.co.baconi.session.SessionService

@Composable
fun MainScreen() {

    val sessionManager = remember { SessionManager() }

    MaterialTheme {
        when(val session = sessionManager.session.collectAsState().value) {
            null -> LoadingScreen()
            else -> SessionScreen(session)
        }
    }

    LaunchedEffect(Unit) {
        delay(250)
        sessionManager.startLoginFlow()
    }
}