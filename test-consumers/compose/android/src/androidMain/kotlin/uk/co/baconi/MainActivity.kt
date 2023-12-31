package uk.co.baconi

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.ktor.http.*
import kotlinx.coroutines.flow.*
import uk.co.baconi.session.SessionManagerEx
import uk.co.baconi.session.oauth.CodeVerifier
import uk.co.baconi.session.oauth.State

class MainActivity : ComponentActivity() {

    companion object {

        private const val TAG = "MainActivity"

        private val Context.oAuthDataStore by preferencesDataStore(name = "oAuth")

        private val STATE = stringPreferencesKey("state")
        private val CODE_VERIFIER = stringPreferencesKey("code_verifier")

        private fun DataStore<Preferences>.get(key: Preferences.Key<String>): Flow<String> {
            return data.mapNotNull { it[key] }
        }

        private suspend fun DataStore<Preferences>.set(key: Preferences.Key<String>, value: String?) {
            edit {
                when (value) {
                    null -> it.remove(key)
                    else -> it[key] = value
                }
            }
        }
    }

    private val sessionManager = SessionManagerEx()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MainScreen(sessionManager)

            LaunchedEffect(Unit) {

                val state = oAuthDataStore.get(STATE).map(::State).firstOrNull()
                val verifier = oAuthDataStore.get(CODE_VERIFIER).map(::CodeVerifier).firstOrNull()
                val callback = intent.data?.toString()?.let(::Url)

                when {
                    state == null || verifier == null || callback == null -> {

                        val newState = sessionManager.sessionService.createState().also {
                            oAuthDataStore.set(STATE, it.value)
                        }
                        val newVerifier = sessionManager.sessionService.createVerifier().also {
                            oAuthDataStore.set(CODE_VERIFIER, it.value)
                        }

                        Log.d(TAG, "startLogin($newState, $newVerifier)")
                        sessionManager.startLogin(this@MainActivity, newState, newVerifier)
                    }
                    // TODO - Replace with remembered and persisted storage, probably AccountManager?
                    sessionManager.session.firstOrNull() == null -> {
                        Log.d(TAG, "handleCallback($callback, $state, $verifier)")
                        sessionManager.handleCallback(callback, state, verifier)
                    }
                }
            }
        }
    }
}