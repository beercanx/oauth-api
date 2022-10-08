package uk.co.baconi.oauth.api.exchange.grants.authorisation

import uk.co.baconi.oauth.api.Repository
import java.util.*

interface AuthorisationCodeRepository : Repository<AuthorisationCode, UUID>