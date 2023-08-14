package uk.co.baconi.oauth.api.common.html

import kotlinx.html.*
import java.util.UUID

object PageTemplate {

    fun HTML.base() {
        lang = "en"
    }

    fun HEAD.metaData() {
        meta { charset = "utf-8" }
        meta { name = "viewport"; content = "width=device-width, initial-scale=1" }
    }

    fun HEAD.csrfToken(csrfToken: UUID) {
        meta("_csrf", csrfToken.toString())
    }

    fun HEAD.bootstrap() {
        link {
            rel = "stylesheet"
            crossorigin = "anonymous"
            href = "https://cdn.jsdelivr.net/npm/bootstrap@5.2.0/dist/css/bootstrap.min.css"
            integrity = "sha384-gH2yIJqKdNHPEq0n4Mqa/HGKIhSkIHeL5AyhkYV8i59U5AR6csBvApHHNl/vI1Bx"
        }
    }
}