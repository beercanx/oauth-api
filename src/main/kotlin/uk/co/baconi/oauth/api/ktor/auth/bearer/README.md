# Bearer Auth Support

As far as I can tell, Ktor does not provide a basic auth support for Bearer authentication tokens, for use when building your own.

This has lead to cloning the Basic Auth provider and making it work for basic bearer token support.
https://raw.githubusercontent.com/ktorio/ktor/2.0.2/ktor-server/ktor-server-plugins/ktor-server-auth/jvmAndNix/src/io/ktor/server/auth/BasicAuth.kt
