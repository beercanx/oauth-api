val ktorVersion: String by project
val coroutinesVersion: String by project

plugins {
    kotlin("js")
    kotlin("plugin.serialization")
}

kotlin {
    js {
        browser()
    }
}

dependencies {
    api(project(":user-interface:common"))
}