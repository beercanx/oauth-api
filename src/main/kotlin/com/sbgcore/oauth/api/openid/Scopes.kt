package com.sbgcore.oauth.api.openid

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Scopes : SerializableEnum {

    @SerialName("openid") OpenId, // Can I get user info with this client?

    // TODO - Break down into know groups or edge cases.
    @SerialName("profile::read") ProfileRead, // Can I read my profile data with this client?
    @SerialName("profile::write") ProfileWrite, // Can I make changes to my profile with this client?

    // TODO - Rethink maybe, might not be as cut and dry and enables verifying vs indicating it needs doing.
    @SerialName("account::verify") AccountVerify, // Can I verify my account? (aka have I verified or not)
    @SerialName("account::upgrade") AccountUpgrade, // Can I upgrade my account? (ditto)

    @SerialName("bet::place") BetPlace, // Can I place a bet with this client?

    @SerialName("bet::promotion::claim") BetPromotionClaim, // Can I claim a bet promotion with this client?

    @SerialName("game::view::restricted") GameViewRestricted, // Can I view restricted game content with this client?

    @SerialName("game::play::free") GamePlayFree, // Can I play free games with this client?
    @SerialName("game::play::paid") GamePlayPaid, // Can I play paid games with this client?

    @SerialName("game::promotion::claim") GamePromotionClaim, // Can I claim a game promotion with this client?

    ;

    val value: String = getSerialName() // TODO - Decide if we even need do expose this.
}