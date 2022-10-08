package uk.co.baconi.oauth.api.common.scope

import uk.co.baconi.oauth.api.common.claim.Claim

data class ScopeConfiguration(val id: Scope, val claims: Set<Claim>) {

    /**
     * Generated based on its database ID field [id].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ScopeConfiguration

        if (id != other.id) return false

        return true
    }

    /**
     * Generated based on its database ID field [id].
     */
    override fun hashCode(): Int {
        return id.hashCode()
    }
}
