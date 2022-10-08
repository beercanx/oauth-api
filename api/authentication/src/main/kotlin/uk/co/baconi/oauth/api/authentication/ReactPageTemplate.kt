package uk.co.baconi.oauth.api.authentication

import kotlinx.html.*

// TODO - Move into common
// TODO - Consider the Template<HTML> method, does this current method impact performance?
//        https://ktor.io/docs/html-dsl.html#templates
object ReactPageTemplate {

    private const val EMPTY = ""

    private var LINK.crossorigin: String
        get() = attributes["crossorigin"] ?: EMPTY
        set(newValue) {
            attributes["crossorigin"] = newValue
        }

    fun HTML.reactPageTemplate(title: String, reactSource: String) {
        lang = "en"
        head {
            meta(charset = "utf-8")
            meta(name = "viewport", content = "width=device-width, initial-scale=1")
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