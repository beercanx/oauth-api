# Bearer Auth Support

As far as I can tell, Ktor does not provide auth support for Bearer authentication tokens.

This has lead to cloning the Basic Auth provider and making it work for basic bearer token support.
https://raw.githubusercontent.com/ktorio/ktor/1.5.2/ktor-features/ktor-auth/jvm/src/io/ktor/auth/BasicAuth.kt
