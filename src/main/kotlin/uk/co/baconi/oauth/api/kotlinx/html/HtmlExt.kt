package uk.co.baconi.oauth.api.kotlinx.html

import kotlinx.html.LINK
import kotlinx.html.SCRIPT

var SCRIPT.crossorigin: String
    get() = checkNotNull(attributes["crossorigin"])
    set(newValue) {
        attributes["crossorigin"] = newValue
    }

var LINK.crossorigin: String
    get() = checkNotNull(attributes["crossorigin"])
    set(newValue) {
        attributes["crossorigin"] = newValue
    }
