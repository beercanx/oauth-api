import com.github.gradle.node.NodeExtension
import com.github.gradle.node.npm.task.NpmTask

plugins {
    alias(libs.plugins.node.gradle) apply false
}

subprojects {

    // Only applies the following configuration deploy if the project has the "node-gradle" plugin defined and enabled.
    plugins.withId(rootProject.libs.plugins.node.gradle.get().pluginId) {

        // Configure the Node plugin
        extensions.configure<NodeExtension> {
            version.set(rootProject.libs.versions.node)
            download.set(System.getenv("CI").toBoolean())
        }

        // Create a clean task to clean up the node directories
        val npmClean = tasks.register<Delete>("npmClean") {
            delete("node_modules", "build")
        }

        // Create a test task to run tests using npm
        val npmTest = tasks.register<NpmTask>("npmTest") {
            dependsOn(tasks.named("npmInstall"))

            args.set(listOf("run", "test"))

            inputs.dir("src")
            inputs.dir("config")
            inputs.dir("public")
            inputs.dir("node_modules")
            inputs.files("tsconfig.json", "scripts/test.js")

            outputs.upToDateWhen { true }
        }

        // Create a build task to build a React bundle using npm
        val npmBuild = tasks.register<NpmTask>("npmBuild") {
            dependsOn(npmTest)

            args.set(listOf("run", "build"))

            inputs.dir(project.fileTree("src").exclude("**/*.test.tsx"))
            inputs.dir("config")
            inputs.dir("public")
            inputs.dir("node_modules")
            inputs.files("tsconfig.json", "scripts/build.js")

            outputs.dir("build/static")
            outputs.files("build/asset-manifest.json")
            outputs.files("build/index.html")
            outputs.upToDateWhen { true }
        }

        // Creates a named bundle based on the projects name
        val renameBundle = tasks.register<Copy>("renameBundle") {
            dependsOn(npmBuild)
            from(project.fileTree("build/static/js").include("main.*.js"))
            into("build/static/bundle")
            rename("""main\.([^.]+)\.js""", "${project.name}.$1.js")
        }

        tasks.register<Task>("clean") {
            dependsOn(npmClean)
        }

        tasks.register<Task>("test") {
            dependsOn(npmTest)
        }

        tasks.register<Task>("build") {
            dependsOn(renameBundle)
        }
    }
}
