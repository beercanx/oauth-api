package uk.co.baconi.session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp

@Composable
fun SessionScreen(session: Session) {
    Box(
        modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)
    ) {
        Column {
            Text(text = "Session", style = MaterialTheme.typography.titleLarge, modifier = Modifier.align(CenterHorizontally))
            Column {
                entry("Token Type:", session.tokenType)
                entry("Access Token:", session.accessToken.value)
                entry("Refresh Token:", session.refreshToken.value)
                entry("Expires In:", "${session.expires}")
                entry("Scopes:", session.scopes.joinToString(" "))
                entry("State:", session.state?.value ?: "")
            }
        }
    }
}

@Composable
private fun entry(title: String, value: String) {
    Row {
        Text(text = title, modifier = Modifier.width(150.dp))
        Text(text = value)
    }
}