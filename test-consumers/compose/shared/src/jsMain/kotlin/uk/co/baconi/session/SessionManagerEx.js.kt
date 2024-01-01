package uk.co.baconi.session

import io.ktor.http.*
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.dom.Storage
import uk.co.baconi.session.oauth.AuthorisationCode
import uk.co.baconi.session.oauth.CodeVerifier
import uk.co.baconi.session.oauth.State

actual class SessionManagerEx actual constructor(private val sessionService: SessionService) : SessionManagerShared() {

    companion object {
        private const val STATE = "STATE"
        private const val VERIFIER = "VERIFIER"
        private const val SESSION = "SESSION"

        private val json = Json {
            prettyPrint = false
            ignoreUnknownKeys = true
        }

        private inline fun <reified T> Storage.get(key: String): T? = getItem(key)?.let(json::decodeFromString)
        private inline fun <reified T> Storage.set(key: String, value: T?) = when (value) {
            null -> removeItem(key)
            else -> setItem(key, json.encodeToString(value))
        }
    }

    init {
        mutableSession.value = currentSession
    }

    private var currentSession: Session?
        get() = localStorage.get(SESSION)
        set(value) = localStorage.set(SESSION, value).also { mutableSession.value = value }

    private var state: State?
        get() = localStorage.get(STATE)
        set(value) = localStorage.set(STATE, value)

    private var verifier: CodeVerifier?
        get() = localStorage.get(VERIFIER)
        set(value) = localStorage.set(VERIFIER, value)

    actual suspend fun startLogin() {
        val state = sessionService.createState().also { state = it }
        val verifier = sessionService.createVerifier().also { verifier = it }
        val challenge = sessionService.createChallenge(verifier)
        val authoriseUrl = sessionService.authoriseUrl(state, challenge)
        window.location.assign(authoriseUrl.toString())
    }

    suspend fun handleCallback(callback: Url) {

        val code = checkNotNull(callback.parameters["code"]?.let(::AuthorisationCode)) {
            "Invalid Code!"
        }

        val callbackState = callback.parameters["state"]
        if (callbackState == null || callbackState != state?.value) {
            throw RuntimeException("Invalid State!")
        }

        val verifier = checkNotNull(verifier) {
            "Invalid Verifier!"
        }

        val session = sessionService.authorisationCodeGrant(code, verifier)
        if (session.state == null || session.state.value != state?.value) {
            throw RuntimeException("Invalid State!")
        }

        currentSession = session
    }
}