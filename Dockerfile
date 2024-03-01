ARG JAVA_VERSION=17
ARG ALPINE_VERSION=3.15
ARG ARGON2_VERSION=20190702

FROM amazoncorretto:${JAVA_VERSION}-alpine${ALPINE_VERSION}-jdk AS code-build

WORKDIR /project

# Add gradlew to enable gradle caching.
COPY gradle /project/gradle/
COPY gradlew /project/

RUN --mount=type=cache,target=/root/.gradle \
    --mount=type=cache,target=/project/.gradle \
    CI="true" ./gradlew

# Add build files to enable dependency resolution caching.
COPY build.gradle.kts settings.gradle.kts gradle.properties /project/
COPY api/assets/build.gradle.kts  /project/api/assets/
COPY api/authentication/build.gradle.kts  /project/api/authentication/
COPY api/authorisation/build.gradle.kts  /project/api/authorisation/
COPY api/common/build.gradle.kts  /project/api/common/
COPY api/server/build.gradle.kts  /project/api/server/
COPY api/session-info/build.gradle.kts  /project/api/session-info/
COPY api/token/build.gradle.kts  /project/api/token/
COPY api/token-introspection/build.gradle.kts  /project/api/token-introspection/
COPY api/token-revocation/build.gradle.kts  /project/api/token-revocation/
COPY user-interface/build.gradle.kts  /project/user-interface/
COPY user-interface/authentication/build.gradle.kts /project/user-interface/authentication/
RUN --mount=type=cache,target=/root/.gradle \
    --mount=type=cache,target=/project/.gradle \
    CI="true" ./gradlew -PuseArgon2NoLibs=true dependencies

# Add the project and build it
COPY api /project/api
COPY user-interface /project/user-interface

# Install the native argon2 C library
ARG ARGON2_VERSION
RUN apk --no-cache add "argon2-libs>${ARGON2_VERSION}"

# Build the project
RUN --mount=type=cache,target=/root/.gradle \
    --mount=type=cache,target=/project/.gradle \
    --mount=type=cache,target=/project/api/assets/build \
    --mount=type=cache,target=/project/api/authentication/build \
    --mount=type=cache,target=/project/api/authorisation/build \
    --mount=type=cache,target=/project/api/common/build \
    --mount=type=cache,target=/project/api/server/build \
    --mount=type=cache,target=/project/api/session-info/build \
    --mount=type=cache,target=/project/api/token/build \
    --mount=type=cache,target=/project/api/token-introspection/build \
    --mount=type=cache,target=/project/api/token-revocation/build \
    --mount=type=cache,target=/project/build \
    --mount=type=cache,target=/project/user-interface/build \
    --mount=type=cache,target=/project/user-interface/node_modules \
    CI="true" ./gradlew -PuseArgon2NoLibs=true build \
    # Unzip all the distributions ready for copying later on \
    && mkdir /project/distributions && cd /project/distributions \
    && for archive in /project/api/*/build/distributions/*.zip; do unzip "$archive"; done

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
COPY --from=code-build /project/distributions/server/ /application
WORKDIR /application
CMD "./bin/server"

## Create Server - Assets
FROM server-base as server-assets
COPY --from=code-build /project/distributions/assets/ /application
WORKDIR /application
CMD "./bin/assets"

## Create Server - Authentication
FROM server-base as server-authentication
COPY --from=code-build /project/distributions/authentication/ /application
WORKDIR /application
CMD "./bin/authentication"

## Create Server - Authorisation
FROM server-base as server-authorisation
COPY --from=code-build /project/distributions/authorisation/ /application
WORKDIR /application
CMD "./bin/authorisation"

## Create Server - Session Info
FROM server-base as server-session-info
COPY --from=code-build /project/distributions/session-info/ /application
WORKDIR /application
CMD "./bin/session-info"

## Create Server - Token
FROM server-base as server-token
COPY --from=code-build /project/distributions/token/ /application
WORKDIR /application
CMD "./bin/token"

## Create Server - Token Introspection
FROM server-base as server-token-introspection
COPY --from=code-build /project/distributions/token-introspection/ /application
WORKDIR /application
CMD "./bin/token-introspection"

## Create Server - Token Revocation
FROM server-base as server-token-revocation
COPY --from=code-build /project/distributions/token-revocation/ /application
WORKDIR /application
CMD "./bin/token-revocation"
