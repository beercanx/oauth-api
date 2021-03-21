package com.sbgcore.oauth.api.openid.exchange.flows.authorization

import com.sbgcore.oauth.api.Repository
import java.util.*

interface AuthorizationCodeRepository : Repository<AuthorizationCode, UUID>