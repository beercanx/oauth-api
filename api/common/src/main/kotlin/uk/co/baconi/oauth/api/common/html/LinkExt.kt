package uk.co.baconi.oauth.api.common.html

import kotlinx.html.LINK

private const val EMPTY = ""

var LINK.crossorigin: String
    get() = attributes["crossorigin"] ?: EMPTY
    set(newValue) {
        attributes["crossorigin"] = newValue
    }