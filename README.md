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
This project was based on the CORE/gradle-kotlin-starter and the Ktor Project Generator.
 * https://stash.skybet.net/projects/CORES/repos/gradle-kotlin-starter/browse
 * https://ktor.io/quickstart/generator.html
 
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
