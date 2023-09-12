package uk.co.baconi

import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import uk.co.baconi.loading.LoadingScreen
import uk.co.baconi.session.Session
import uk.co.baconi.session.SessionScreen
import uk.co.baconi.session.SessionService

@Composable
fun MainScreen() {

    var sessionState: Session? by remember { mutableStateOf(null) }

    when(val session = sessionState) {
        null -> LoadingScreen()
        else -> SessionScreen(session)
    }

    LaunchedEffect(Unit) {
        delay(250)
        sessionState = SessionService().getSession("aardvark", "121212")
    }
}