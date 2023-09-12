package uk.co.baconi

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import uk.co.baconi.session.SessionService

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //LoadingScreen("Android")
            LaunchedEffect(Unit) {
                val sessionService = SessionService(createHttpClient(), "http://10.0.2.2:8080/token")
                sessionService.getSession("aardvark", "121212")
            }
        }
    }
}