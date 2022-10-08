package uk.co.baconi.oauth.api.openid.exchange.grants.authorization

import uk.co.baconi.oauth.api.Repository
import java.util.*

interface AuthorizationCodeRepository : Repository<AuthorizationCode, UUID>