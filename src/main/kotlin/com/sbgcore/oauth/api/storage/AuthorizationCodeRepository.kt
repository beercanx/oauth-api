package com.sbgcore.oauth.api.storage

import com.sbgcore.oauth.api.openid.exchange.flows.authorization.AuthorizationCode
import java.util.*

interface AuthorizationCodeRepository : Repository<AuthorizationCode, UUID>