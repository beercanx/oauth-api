package com.sbgcore.oauth.api.openid

import com.sbgcore.oauth.api.enums.WithValue
import com.sbgcore.oauth.api.serializers.ScopeSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ScopeSerializer::class)
enum class Scopes(override val value: String, val claims: Set<Claims> = emptySet()) : WithValue {

    OpenId("openid"), // Can I get user info with this client?

    // TODO - Break down into know groups or edge cases.
    ProfileRead("profile::read"), // Can I read my profile data with this client?
    ProfileWrite("profile::write"), // Can I make changes to my profile with this client?

    // TODO - Rethink maybe, might not be as cut and dry and enables verifying vs indicating it needs doing.
    AccountVerify("account::verify"), // Can I verify my account? (aka have I verified or not)
    AccountUpgrade("account::upgrade"), // Can I upgrade my account? (ditto)

    BetPlace("bet::place"), // Can I place a bet with this client?

    BetPromotionClaim("bet::promotion::claim"), // Can I claim a bet promotion with this client?

    GameViewRestricted("game::view::restricted"), // Can I view restricted game content with this client?

    GamePlayFree("game::play::free"), // Can I play free games with this client?
    GamePlayPaid("game::play::paid"), // Can I play paid games with this client?

    GamePromotionClaim("game::promotion::claim"), // Can I claim a game promotion with this client?

    ;
}