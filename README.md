# Gradle and Kotlin Example

## To setup a new project with a Gradle wrapper, if you've installed Gradle into your path.
```
gradle wrapper --gradle-version 5.4.1 --distribution-type all
```

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
