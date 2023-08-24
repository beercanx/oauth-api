package uk.co.baconi.oauth.automation.api

import com.typesafe.config.Config
import java.net.URI

fun Config.getUri(path: String): URI = getString(path).let(URI::create)