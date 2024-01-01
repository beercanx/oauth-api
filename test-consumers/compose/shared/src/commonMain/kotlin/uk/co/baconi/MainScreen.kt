package uk.co.baconi

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import uk.co.baconi.loading.LoadingScreen
import uk.co.baconi.session.SessionManagerEx
import uk.co.baconi.session.SessionScreen

@Composable
fun MainScreen(sessionManager: SessionManagerEx) {

    MaterialTheme {
        val session = sessionManager.session.collectAsState().value
        when {
            // TODO - Consider having a start authentication screen that allows config changes.
            session != null -> SessionScreen(session)
            else -> LoadingScreen()
        }
    }
}