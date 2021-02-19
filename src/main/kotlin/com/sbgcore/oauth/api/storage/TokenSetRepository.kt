package com.sbgcore.oauth.api.storage

import com.sbgcore.oauth.api.openid.exchange.TokenSet
import com.sbgcore.oauth.api.openid.flows.authorization.AuthorizationCode
import java.util.*

interface TokenSetRepository : Repository<TokenSet, UUID> {
}