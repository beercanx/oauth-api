package uk.co.baconi.oauth.api.ktor

import io.ktor.server.application.*
import io.ktor.util.pipeline.*

typealias ApplicationContext = PipelineContext<Unit, ApplicationCall>
