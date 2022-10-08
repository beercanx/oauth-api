ARG JAVA_VERSION=17
ARG ALPINE_VERSION=3.15
ARG ARGON2_VERSION=20190702

FROM amazoncorretto:${JAVA_VERSION}-alpine${ALPINE_VERSION}-jdk AS code-build

WORKDIR /project

# Add gradlew to enable gradle caching.
COPY gradle /project/gradle/
COPY gradlew /project/
RUN ./gradlew

# Add build files to enable dependency resolution caching.
COPY build.gradle.kts settings.gradle.kts gradle.properties /project/
COPY api/authentication/build.gradle.kts  /project/api/authentication/
COPY api/authorisation/build.gradle.kts  /project/api/authorisation/
COPY api/common/build.gradle.kts  /project/api/common/
COPY api/server/build.gradle.kts  /project/api/server/
COPY api/session-info/build.gradle.kts  /project/api/session-info/
COPY api/token/build.gradle.kts  /project/api/token/
COPY api/token-introspection/build.gradle.kts  /project/api/token-introspection/
COPY api/token-revocation/build.gradle.kts  /project/api/token-revocation/
COPY api/user-info/build.gradle.kts  /project/api/user-info/
COPY api/well-known/build.gradle.kts  /project/api/well-known/
RUN ./gradlew -Pargon2Type=argon2-jvm-nolibs dependencies

# Add the project and build it
COPY api /project/api

# Install the native argon2 C library
ARG ARGON2_VERSION
RUN apk --no-cache add "argon2-libs>${ARGON2_VERSION}"

# Build the project
RUN ./gradlew -Pargon2Type=argon2-jvm-nolibs build

RUN cd /project/api/server/build/distributions && unzip server-*.zip

FROM amazoncorretto:${JAVA_VERSION}-alpine${ALPINE_VERSION}-jdk AS jre-build

# Fix Alpine -- missing objcopy
RUN apk add --no-cache binutils

# Create the smallest JRE that we need
RUN $JAVA_HOME/bin/jlink \
  --verbose \
  --add-modules java.base,java.xml,java.naming,java.sql \
  --strip-debug \
  --no-man-pages \
  --no-header-files \
  --compress=2 \
  --output /javaruntime

FROM alpine:${ALPINE_VERSION} AS server-base

# Make sure there's no out standing OS updates to install
ARG ARGON2_VERSION
RUN apk --no-cache upgrade && \
    apk --no-cache add java-common "argon2-libs>${ARGON2_VERSION}"

# Setup the JRE
ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"
COPY --from=jre-build /javaruntime $JAVA_HOME

## Create Server - Full
FROM server-base as server-full

## Copy over the full server code
COPY --from=code-build /project/api/server/build/distributions/server-*/ /application

WORKDIR /application

CMD "./bin/server"

## TODO - Create Server - Authentication
## TODO - Create Server - Authorisation
## TODO - Create Server - Common
## TODO - Create Server - Server
## TODO - Create Server - Session Info
## TODO - Create Server - Token
## TODO - Create Server - Token Introspection
## TODO - Create Server - Token Revocation
## TODO - Create Server - User Info
## TODO - Create Server - Well Known
