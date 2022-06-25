package uk.co.baconi.oauth.api.wellknown

import io.ktor.resources.*

@kotlinx.serialization.Serializable
@Resource("/.well-known/product-configuration")
class ProductConfigurationLocation