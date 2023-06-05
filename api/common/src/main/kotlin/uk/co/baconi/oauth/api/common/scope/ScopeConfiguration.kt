package uk.co.baconi.oauth.api.common.scope

import uk.co.baconi.oauth.api.common.claim.Claim

data class ScopeConfiguration(val id: Scope, val claims: Set<Claim>)