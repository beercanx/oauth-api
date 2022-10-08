plugins {
    base
    kotlin("jvm") version "1.7.0" apply false
    kotlin("plugin.serialization") version "1.7.0" apply false
}

allprojects {

    group = "uk.co.baconi.oauth"
    version = "0.1"

    repositories {
        mavenCentral()
    }
}

tasks.withType<Wrapper>().configureEach {
    distributionType = Wrapper.DistributionType.ALL
}