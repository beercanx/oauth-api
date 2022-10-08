package uk.co.baconi.oauth.api.common.html

import kotlinx.html.*

object ReactTemplate {

    fun HTML.reactPage(title: String, reactSource: String) {
        lang = "en"
        head {
            meta { charset = "utf-8" }
            meta { name = "viewport"; content = "width=device-width, initial-scale=1" }
            title { +title }
            link {
                rel = "stylesheet"
                crossorigin = "anonymous"
                href = "https://cdn.jsdelivr.net/npm/bootstrap@5.2.0/dist/css/bootstrap.min.css"
                integrity = "sha384-gH2yIJqKdNHPEq0n4Mqa/HGKIhSkIHeL5AyhkYV8i59U5AR6csBvApHHNl/vI1Bx"
            }
            script { defer = true; src = reactSource }
        }
        body {
            noScript { +"You need to enable JavaScript to access this website." }
            div(classes = "container") { id = "root" }
        }
    }
}