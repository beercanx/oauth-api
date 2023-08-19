# A Ktor bare-bones OAuth API

## Using Gradle
```bash
# To run tests for a project, Gradle will not run tests for area's that have not had any changes.
./gradlew test

# To build an application bundle, after compiling and running test.
./gradlew build

# Remove previous build cache files, useful to force recompilation and test.
./gradlew clean

# Run the basic application
./gradlew run
```

## Building Docker
Since it's a multistage project we need to run a few commands to build and tag everything.

```bash
# Create all the all endpoints server instance
docker build --tag 'oauth-api:server-full' --target server-full .

# Create all the individual server instances [not are implemented yet have]
docker build --tag 'oauth-api:server-assets' --target server-assets .
docker build --tag 'oauth-api:server-authentication' --target server-authentication .
docker build --tag 'oauth-api:server-authorisation' --target server-authorisation .
docker build --tag 'oauth-api:server-session-info' --target server-session-info .
docker build --tag 'oauth-api:server-token' --target server-token .
docker build --tag 'oauth-api:server-token-introspection' --target server-token-introspection .
docker build --tag 'oauth-api:server-token-revocation' --target server-token-revocation .
```

## Sources
OAuth and related RFCs
 * https://www.rfc-editor.org/rfc/rfc6749 - The OAuth 2.0 Authorization Framework
 * https://www.rfc-editor.org/rfc/rfc8252 - OAuth 2.0 for Native Apps
 * https://www.rfc-editor.org/rfc/rfc7662 - OAuth 2.0 Token Introspection
 * https://www.rfc-editor.org/rfc/rfc7009 - OAuth 2.0 Token Revocation
 * https://www.rfc-editor.org/rfc/rfc6750 - The OAuth 2.0 Authorization Framework: Bearer Token Usage
 * https://www.rfc-editor.org/rfc/rfc7521 - Assertion Framework for OAuth 2.0 Client Authentication and Authorization Grants
 * https://www.rfc-editor.org/rfc/rfc7523 - JSON Web Token (JWT) Profile for OAuth 2.0 Client Authentication and Authorization Grants
 * https://www.rfc-editor.org/rfc/rfc7636 - Proof Key for Code Exchange by OAuth Public Clients