package uk.co.baconi.oauth.api.common.html

import io.kotest.matchers.shouldBe
import kotlinx.html.html
import kotlinx.html.stream.appendHTML
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.html.ReactTemplate.reactPage
import java.util.*

class ReactTemplateIntegrationTest {

    private fun underTest(csrfToken: UUID? = null) = buildString {
        appendHTML().html {
            reactPage(title = "Custom Title", reactSource = "file://source.js")
        }
    }

    @Test
    fun `should be able to render a templated react HTML page`() {

        buildString {
            appendHTML().html {
                reactPage(title = "Custom Title", reactSource = "file://source.js")
            }
        } shouldBe """
            |<html lang="en">
            |  <head>
            |    <meta charset="utf-8">
            |    <meta name="viewport" content="width=device-width, initial-scale=1">
            |    <link rel="stylesheet" crossorigin="anonymous" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.0/dist/css/bootstrap.min.css" integrity="sha384-gH2yIJqKdNHPEq0n4Mqa/HGKIhSkIHeL5AyhkYV8i59U5AR6csBvApHHNl/vI1Bx">
            |    <title>Custom Title</title>
            |    <script defer="defer" src="file://source.js"></script>
            |  </head>
            |  <body>
            |    <noscript>You need to enable JavaScript to access this website.</noscript>
            |    <div class="container" id="root"></div>
            |  </body>
            |</html>
            |""".trimMargin()
    }

    @Test
    fun `should be able to render a templated react HTML page with a csrf token`() {

        val csrfToken = UUID.randomUUID()

        buildString {
            appendHTML().html {
                reactPage(title = "Custom Title", reactSource = "file://source.js", csrfToken = csrfToken)
            }
        } shouldBe """
            |<html lang="en">
            |  <head>
            |    <meta charset="utf-8">
            |    <meta name="viewport" content="width=device-width, initial-scale=1">
            |    <meta name="_csrf" content="$csrfToken">
            |    <link rel="stylesheet" crossorigin="anonymous" href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.0/dist/css/bootstrap.min.css" integrity="sha384-gH2yIJqKdNHPEq0n4Mqa/HGKIhSkIHeL5AyhkYV8i59U5AR6csBvApHHNl/vI1Bx">
            |    <title>Custom Title</title>
            |    <script defer="defer" src="file://source.js"></script>
            |  </head>
            |  <body>
            |    <noscript>You need to enable JavaScript to access this website.</noscript>
            |    <div class="container" id="root"></div>
            |  </body>
            |</html>
            |""".trimMargin()
    }
}