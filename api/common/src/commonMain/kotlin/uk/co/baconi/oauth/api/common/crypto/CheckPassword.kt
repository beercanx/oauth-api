package uk.co.baconi.oauth.api.common.crypto

typealias CheckHashedPassword = (hash: String, password: CharArray) -> Boolean

expect object CheckPassword {
    val checkHashedPassword: CheckHashedPassword
}
