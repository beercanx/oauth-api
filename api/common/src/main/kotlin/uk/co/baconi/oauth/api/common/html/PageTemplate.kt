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
            href = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css"
            integrity = "sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB"
        }
    }
}