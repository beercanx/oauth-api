val kotlinWrappersVersion: String by project

plugins {
    kotlin("js")
}

kotlin {
    js {
        browser {
            commonWebpackConfig { // TODO - Do we need webpack in a pure library?
                cssSupport.enabled = true
            }
        }
    }
}

dependencies {
    // Kotlin Wrappers BOM - Handles individual wrapper versioning
    api(enforcedPlatform(kotlinWrapper("wrappers-bom:$kotlinWrappersVersion")))

    // React + React Dom
    api(kotlinWrapper("react"))
    api(kotlinWrapper("react-dom"))
}