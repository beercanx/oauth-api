# A Ktor bare bones OAuth API

## Using Gradle
```
# To run tests for a project, Gradle will not run tests for area's that have not had any changes.
./gradlew test

# To build an application bundle, after compiling and running test.
./gradlew build

# Remove previous build cache files, useful to force recompilation and test.
./gradlew clean

# Run the basic application
./gradlew run
```

## Use Docker Compose
```
# Build the image
docker-compose build

# Start the basic application in Docker
docker-compose up
```

## Sources
OAuth and related RFCs
 * https://tools.ietf.org/html/rfc6749 - The OAuth 2.0 Authorization Framework
 * https://tools.ietf.org/html/rfc8252 - OAuth 2.0 for Native Apps
 * https://tools.ietf.org/html/rfc7662 - OAuth 2.0 Token Introspection
 * https://tools.ietf.org/html/rfc7009 - OAuth 2.0 Token Revocation
 * https://tools.ietf.org/html/rfc6750 - The OAuth 2.0 Authorization Framework: Bearer Token Usage
 * https://tools.ietf.org/html/rfc7521 - Assertion Framework for OAuth 2.0 Client Authentication and Authorization Grants
 * https://tools.ietf.org/html/rfc7523 - JSON Web Token (JWT) Profile for OAuth 2.0 Client Authentication and Authorization Grants
 * https://tools.ietf.org/html/rfc7636 - Proof Key for Code Exchange by OAuth Public Clients
 
These Ktor features have been picked as they looked useful:
 * https://ktor.io/servers/features/routing.html
 * https://ktor.io/servers/features/locations.html
 * https://ktor.io/servers/features/metrics.html
 * https://ktor.io/servers/features/sessions.html
 * https://ktor.io/servers/features/compression.html
 * https://ktor.io/servers/features/autoheadresponse.html
 * https://ktor.io/servers/features/caching-headers.html
 * https://ktor.io/servers/features/data-conversion.html
 * https://ktor.io/servers/features/forward-headers.html
 * https://ktor.io/servers/features/hsts.html
 * https://ktor.io/servers/features/status-pages.html
 * https://ktor.io/servers/features/content-negotiation/serialization-converter.html
 * https://ktor.io/clients/index.html
 * https://ktor.io/clients/http-client/features/json-feature.html

Ktor Views:
 * https://ktor.io/docs/html-dsl.html
 * https://getbootstrap.com/docs/5.0/forms/overview

Gatling resources:
 * https://gatling.io/docs/current/http/http_request
 * https://gatling.io/docs/current/session/feeder
 * https://gatling.io/docs/current/http/http_check
 * https://gatling.io/docs/current/session/session_api
 * https://github.com/gatling/gatling-gradle-plugin-demo
 * https://devqa.io/gatling-oath2-authentication

Static assets:
 * https://pixabay.com/vectors/people-character-faces-real-305836/

Future resources:
 * https://github.com/JetBrains/kotlin-wrappers
 * https://tailwindcss.com/docs/installation