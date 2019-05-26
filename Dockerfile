###########################
## Build the source code ##
###########################
FROM openjdk:11-jdk-slim as BUILDER
LABEL maintainer=james.bacon@skybettingandgaming.com

## Copy code for build
COPY src/ /opt/code/src
COPY gradle/ /opt/code/gradle
COPY gradlew /opt/code/gradlew
COPY build.gradle.kts /opt/code/build.gradle.kts
COPY settings.gradle.kts /opt/code/settings.gradle.kts

## Switch to the code
WORKDIR /opt/code

## Compile the project
RUN ./gradlew build

## Extract distribution
WORKDIR /opt/code/build/distributions/
RUN unzip gradle-kotlin-starter.zip

#############################
## Create production image ##
#############################
FROM openjdk:11-jre-slim
LABEL maintainer=james.bacon@skybettingandgaming.com

## Switch to the code
WORKDIR /opt/distribution

## Copy in the uber jar from the builder
COPY --from=BUILDER /opt/code/build/distributions/gradle-kotlin-starter  /opt/distribution

## Setup to run application on start
CMD ["/opt/distribution/bin/gradle-kotlin-starter"]
