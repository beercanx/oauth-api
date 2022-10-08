plugins {
    kotlin("js")
}

kotlin {
    js {
        browser()
    }
}

dependencies {
    api(project(":user-interface:common"))
}