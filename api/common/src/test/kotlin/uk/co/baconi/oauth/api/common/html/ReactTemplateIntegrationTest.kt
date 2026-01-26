package uk.co.baconi.oauth.api.common.html

import io.kotest.matchers.shouldBe
import kotlinx.html.html
import kotlinx.html.stream.appendHTML
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.html.ReactTemplate.reactPage
import java.util.*

class ReactTemplateIntegrationTest {

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
            |    <link rel="stylesheet" crossorigin="anonymous" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB">
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
            |    <link rel="stylesheet" crossorigin="anonymous" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB">
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