ARG JAVA_VERSION=21
ARG NODEJS_VERSION=20
ARG ALPINE_VERSION=3.18
ARG ARGON2_VERSION=20190702

FROM node:${NODEJS_VERSION}-alpine${ALPINE_VERSION} AS alpine-nodejs

FROM amazoncorretto:${JAVA_VERSION}-alpine${ALPINE_VERSION}-jdk AS code-build

WORKDIR /project

# Add gradlew to enable gradle caching.
COPY gradle /project/gradle/
COPY gradlew /project/
RUN ./gradlew --version --console=plain --info

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
COPY user-interface/authentication/package.json /project/user-interface/authentication/
COPY user-interface/authentication/package-lock.json /project/user-interface/authentication/

RUN ./gradlew dependencies -PuseArgon2NoLibs=true --console=plain --info

# Install Alpine NodeJS
RUN apk --no-cache add libstdc++
COPY --from=alpine-nodejs /usr/local/bin /usr/local/bin
COPY --from=alpine-nodejs /usr/local/include /usr/local/include
COPY --from=alpine-nodejs /usr/local/lib /usr/local/lib
RUN node --version && npm --version

# node_modules resolution caching
RUN cd user-interface && ../gradlew npmInstall --console=plain --info

# Add the project and build it
COPY api /project/api
COPY user-interface /project/user-interface

# Install the native argon2 C library
ARG ARGON2_VERSION
RUN apk --no-cache add "argon2-libs>${ARGON2_VERSION}"

# Build the project
RUN ./gradlew build -PuseArgon2NoLibs=true --console=plain --info \
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
