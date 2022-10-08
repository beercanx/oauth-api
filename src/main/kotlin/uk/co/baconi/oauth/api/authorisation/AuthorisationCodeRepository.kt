package uk.co.baconi.oauth.api.authorisation

import uk.co.baconi.oauth.api.Repository
import uk.co.baconi.oauth.api.authorisation.AuthorisationCode
import java.util.*

interface AuthorisationCodeRepository : Repository<AuthorisationCode, String>