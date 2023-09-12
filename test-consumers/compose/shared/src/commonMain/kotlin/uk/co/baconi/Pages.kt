package uk.co.baconi

import uk.co.baconi.session.Session

sealed class Page

class SessionPage(val session: Session) : Page()