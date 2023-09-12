package uk.co.baconi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import uk.co.baconi.loading.LoadingScreen
import uk.co.baconi.session.SessionService

@Composable
fun MainScreen() {

    LoadingScreen()

    // TODO - Switch from loading to screen screen
    LaunchedEffect(Unit) {
        SessionService().getSession("aardvark", "121212")
    }
}