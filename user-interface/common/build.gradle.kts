val kotlinWrappersVersion: String by project

plugins {
    kotlin("js")
}

kotlin {
    js {
        browser()
    }
}

dependencies {
    // Kotlin Wrappers BOM - Handles individual wrapper versioning
    api(enforcedPlatform(kotlinWrapper("wrappers-bom:$kotlinWrappersVersion")))

    // React + React Dom
    api(kotlinWrapper("react"))
    api(kotlinWrapper("react-dom"))
}