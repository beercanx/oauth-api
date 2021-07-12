package uk.co.baconi.oauth.api.kotlinx.html

import kotlinx.html.LINK
import kotlinx.html.SCRIPT

private const val EMPTY = ""

var SCRIPT.crossorigin: String
    get() = attributes["crossorigin"] ?: EMPTY
    set(newValue) {
        attributes["crossorigin"] = newValue
    }

var LINK.crossorigin: String
    get() = attributes["crossorigin"] ?: EMPTY
    set(newValue) {
        attributes["crossorigin"] = newValue
    }
