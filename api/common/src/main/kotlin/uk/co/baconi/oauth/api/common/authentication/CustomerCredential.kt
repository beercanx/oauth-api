package uk.co.baconi.oauth.api.common.authentication

data class CustomerCredential(
    val username: String,
    val hashedSecret: String,
    // TODO - Include created_at: Instant,
    // TODO - Include modified_at: Instant,
) {

    /**
     * Generated to exclude [hashedSecret] from the toString output.
     */
    override fun toString(): String {
        return "CustomerCredential(username='$username', hashedSecret='REDACTED')"
    }
}