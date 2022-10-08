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

# Unzip all the distrubutions ready for copying later on
RUN cd /project/api/server/build/distributions && unzip *.zip

FROM amazoncorretto:${JAVA_VERSION}-alpine${ALPINE_VERSION}-jdk AS jre-build

RUN apk add --no-cache binutils
RUN ${JAVA_HOME}/bin/jlink \
  --verbose \
  --add-modules java.base,java.xml,java.naming,java.sql \
  --strip-debug \
  --no-man-pages \
  --no-header-files \
  --compress=2 \
  --output /javaruntime

FROM alpine:${ALPINE_VERSION} AS server-base
ARG ARGON2_VERSION

RUN apk --no-cache upgrade && \
    apk --no-cache add java-common "argon2-libs>${ARGON2_VERSION}"

ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"
COPY --from=jre-build /javaruntime ${JAVA_HOME}

## Create Server - Full
FROM server-base as server-full
COPY --from=code-build /project/api/server/build/distributions/server-*/ /application
WORKDIR /application
CMD "./bin/server"

## Create Server - Authentication
FROM server-base as server-authentication
COPY --from=code-build /project/api/server/build/distributions/authentication-*/ /application
WORKDIR /application
CMD "./bin/authentication"

## Create Server - Authorisation
FROM server-base as server-authorisation
COPY --from=code-build /project/api/server/build/distributions/authorisation-*/ /application
WORKDIR /application
CMD "./bin/authorisation"

## Create Server - Session Info
FROM server-base as server-session-info
COPY --from=code-build /project/api/server/build/distributions/session-info-*/ /application
WORKDIR /application
CMD "./bin/session-info"

## Create Server - Token
FROM server-base as server-token
COPY --from=code-build /project/api/server/build/distributions/token-*/ /application
WORKDIR /application
CMD "./bin/token"

## Create Server - Token Introspection
FROM server-base as server-token-introspection
COPY --from=code-build /project/api/server/build/distributions/token-introspection-*/ /application
WORKDIR /application
CMD "./bin/token-introspection"

## Create Server - Token Revocation
FROM server-base as server-token-revocation
COPY --from=code-build /project/api/server/build/distributions/token-revocation-*/ /application
WORKDIR /application
CMD "./bin/token-revocation"

## Create Server - User Info
FROM server-base as server-user-info
COPY --from=code-build /project/api/server/build/distributions/user-info-*/ /application
WORKDIR /application
CMD "./bin/user-info"

## Create Server - Well Known
FROM server-base as server-well-known
COPY --from=code-build /project/api/server/build/distributions/well-known-*/ /application
WORKDIR /application
CMD "./bin/well-known"