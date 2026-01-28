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
        val rsbuildClean = tasks.register<Delete>("rsbuildClean") {
            delete("build")
        }

        val npmClean = tasks.register<Delete>("npmClean") {
            dependsOn(rsbuildClean)
            delete("node_modules", "build", "dist", ".parcel-cache")
        }

        // Create a test task to run tests using npm
        val npmTest = tasks.register<NpmTask>("npmTest") {
            dependsOn(tasks.named("npmInstall"))

            args.set(listOf("run", "test"))

            inputs.dir("src")
            inputs.dir("node_modules")
            inputs.files("tsconfig.json", "package.json", "jest.config.js")

            outputs.upToDateWhen { true }
        }

        // Create a build task to build a React bundle using npm
        val npmBuild = tasks.register<NpmTask>("npmBuild") {
            dependsOn(rsbuildClean)
            dependsOn(npmTest)

            args.set(listOf("run", "build"))

            inputs.dir(project.fileTree("src").exclude("**/*.test.tsx"))
            inputs.dir("node_modules")
            inputs.files("tsconfig.json", "package.json")

            outputs.dir("build")
            outputs.upToDateWhen { true }
        }

        tasks.register<Task>("clean") {
            dependsOn(npmClean)
        }

        tasks.register<Task>("test") {
            dependsOn(npmTest)
        }

        tasks.register<Task>("build") {
            dependsOn(npmBuild)
        }
    }
}
