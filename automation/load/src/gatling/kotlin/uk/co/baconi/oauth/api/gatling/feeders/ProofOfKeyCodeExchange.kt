package uk.co.baconi.oauth.api.gatling.feeders

import io.gatling.javaapi.core.Session
import uk.co.baconi.oauth.api.gatling.sessionToString
import java.security.MessageDigest
import java.util.Base64
import kotlin.random.Random

object ProofOfKeyCodeExchange {

    private const val CODE_VERIFIER = "codeVerifier"
    private const val CODE_CHALLENGE = "codeChallenge"
    private const val CODE_CHALLENGE_METHOD = "codeChallengeMethod"

    object Expressions {
        val codeVerifier: (Session) -> String = sessionToString(CODE_VERIFIER)
        val codeChallenge: (Session) -> String = sessionToString(CODE_CHALLENGE)
        val codeChallengeMethod: (Session) -> String = sessionToString(CODE_CHALLENGE_METHOD)
    }

    object Setup {

        private const val VERIFIER_LENGTH = 64
        private const val CHALLENGE_METHOD = "S256"

        private val verifierCharPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        private val base64UrlEncoder = Base64.getUrlEncoder().withoutPadding()

        fun generateVerifierAndChallenge(): (Session) -> Session = { session ->

            val verifier = (1..VERIFIER_LENGTH)
                .map { Random.nextInt(0, verifierCharPool.size).let { verifierCharPool[it] } }
                .joinToString("")

            fun ascii(string: String): ByteArray = string.toByteArray()
            fun sha256(bytes: ByteArray): ByteArray = MessageDigest.getInstance("SHA-256").digest(bytes)
            fun base64UrlEncodeWithoutPadding(bytes: ByteArray): String = base64UrlEncoder.encodeToString(bytes)

            // https://www.rfc-editor.org/rfc/rfc7636#section-4.6
            val challenge = base64UrlEncodeWithoutPadding(sha256(ascii(verifier)))

            session
                .set(CODE_VERIFIER, verifier)
                .set(CODE_CHALLENGE, challenge)
                .set(CODE_CHALLENGE_METHOD, CHALLENGE_METHOD)
        }
    }
}