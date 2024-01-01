package uk.co.baconi

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import io.ktor.http.*
import kotlinx.coroutines.flow.firstOrNull
import uk.co.baconi.session.SessionManagerEx

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val sessionManager = SessionManagerEx(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MainScreen(sessionManager)

            LaunchedEffect(Unit) {
                val callback = intent.data?.toString()?.let(::Url)
                when {
                    // TODO - Replace with remembered and persisted storage, probably AccountManager?
                    sessionManager.session.firstOrNull() != null -> Log.d(TAG, "Already Logged In!")
                    callback == null -> sessionManager.startLogin()
                    else -> sessionManager.handleCallback(callback)
                }
            }
        }
    }
}