package uk.co.baconi.oauth.api.authentication

import io.ktor.application.*
import io.ktor.html.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.routing.*
import kotlinx.html.*
import uk.co.baconi.oauth.api.authentication.AuthenticationPageTemplate.Companion.csrfToken
import uk.co.baconi.oauth.api.kotlinx.html.PageTemplate
import java.util.*


interface AuthenticationRoute {

    // TODO - Consider if we need the pre-authenticated session to be created and destroyed by Authorization instead.

    // TODO - https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html#login-csrf
    // TODO - https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html#synchronizer-token-pattern

    fun Route.authentication() {

        get<AuthenticationLocation> {

            // TODO - Create pre-authenticated session

            // TODO - Add form validation (client-side)

            // TODO - Store in pre-authenticated session
            val csrfToken = UUID.randomUUID()

            call.respondHtmlTemplate(AuthenticationPageTemplate(locations)) {
                csrfToken(csrfToken)
            }
        }

        post<AuthenticationLocation> {

            // TODO - Handle login form submission
            // TODO - Check csrf_token before processing anything

            // TODO - Add form validation (server side)
            //          Respond 400?
            //          Respond with html form filled in and validation messages shown?
            //          How do we tied both client and server side validation?

            val parameters = call.receiveParameters()
            application.log.trace("${call.request.local.uri} with: $parameters")

            // TODO - On successful authentication
            // TODO - Create full session.
            // TODO - Destroy pre-authenticated session.

            call.respondHtmlTemplate(PageTemplate()) {

                pageTitle {
                    +"Authentication - Posted"
                }

                pageContent {
                    p {
                        +"csrf_token: ${parameters["csrf_token"]}"
                    }
                    p {
                        +"username: ${parameters["username"]}"
                    }
                    p {
                        +"password: <REDACTED>"
                    }
                }
            }
        }
    }
}