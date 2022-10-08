package uk.co.baconi.oauth.api.token

// TODO - Can / should public clients have refresh?
//data class RefreshTokenRequest(
//    override val principal: ConfidentialClient,
//    val scopes: Set<Scope>,
//    val refreshToken: String
//) : TokenRequest.Valid<ConfidentialClient> {
//    override fun toString(): String {
//        return "RefreshTokenRequest(principal=$principal, scopes=$scopes, refreshToken='REDACTED')"
//    }
//}