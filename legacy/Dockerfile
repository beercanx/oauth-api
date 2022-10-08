###########################
## Build the source code ##
###########################
FROM openjdk:11-jdk-slim as BUILDER

## Copy code for build
COPY src/ /opt/code/src
COPY gradle/ /opt/code/gradle
COPY gradlew /opt/code/gradlew
COPY build.gradle.kts /opt/code/build.gradle.kts
COPY settings.gradle.kts /opt/code/settings.gradle.kts
COPY gradle.properties /opt/code/gradle.properties

## Switch to the code
WORKDIR /opt/code

## Build the project
RUN apt-get update && \
    # install unzip as its not always available
    apt-get -y install unzip && \
    ./gradlew build

## Extract distribution
WORKDIR /opt/code/build/distributions/
RUN unzip oauth-api.zip

#############################
## Create production image ##
#############################
FROM openjdk:11-jre-slim

## Switch to the code
WORKDIR /opt/distribution

## Copy in the uber jar from the builder
COPY --from=BUILDER /opt/code/build/distributions/oauth-api  /opt/distribution

## Setup to run application on start
CMD ["/opt/distribution/bin/oauth-api"]
