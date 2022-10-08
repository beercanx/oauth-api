plugins {
    kotlin("js")
}

kotlin {
    js {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
        binaries.executable() // TODO - Remove as its probably all going into the ':user-interface:server' module.
    }
}

dependencies {
    api(project(":user-interface:common"))
}

fun kotlinWrapper(target: String): String = "org.jetbrains.kotlin-wrappers:kotlin-$target"