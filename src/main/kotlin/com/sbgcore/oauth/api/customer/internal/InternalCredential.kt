package com.sbgcore.oauth.api.customer.internal

import org.dizitart.no2.objects.Id

data class InternalCredential(@Id val username: String, val secret: String, val temporary: Boolean = false)
