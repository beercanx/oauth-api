package uk.co.baconi.oauth.api.kotlinx.html

import io.ktor.html.*
import kotlinx.html.*

class PageTemplate(
    private val bootstrapCSS: Boolean = true,
    private val bootstrapJS: Boolean = false,
) : Template<HTML> {

    val pageTitle = Placeholder<TITLE>()
    val pageContent = Placeholder<DIV>()

    override fun HTML.apply() {

        lang = "en"

        head {

            meta(charset = "utf-8")
            meta(name = "viewport", content = "width=device-width, initial-scale=1")

            if(bootstrapCSS) {
                // https://getbootstrap.com/docs/5.0/getting-started/introduction/
                link {
                    href = "https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/css/bootstrap.min.css"
                    rel = "stylesheet"
                    integrity = "sha384-+0n0xVW2eSR5OomGNYDnhzAbDsOXxcvSN1TPprVMTNDbiYZCxYbOOl7+AMvyTG2x"
                    crossorigin = "anonymous"
                }
            }

            title {
                insert(pageTitle)
            }
        }
        body {

            div(classes = "container") {
                insert(pageContent)
            }

            if(bootstrapJS) {
                script {
                    src = "https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/js/bootstrap.bundle.min.js"
                    integrity = "sha384-gtEjrD/SeCtmISkJkNUaaKMoLD0//ElJ19smozuHV6z3Iehds+3Ulb9Bn9Plx0x4"
                    crossorigin = "anonymous"
                }
            }
        }
    }
}