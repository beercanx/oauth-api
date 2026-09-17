import com.github.gradle.node.NodeExtension
import com.github.gradle.node.npm.task.NpmTask

plugins {
    alias(libs.plugins.node.gradle) apply false
}

buildscript {
    dependencies {
        // Review on changes to `gradle-node-plugin`, see `../build.gradle.kts`
        for (securityBom in gradle.extra["securityBoms"] as List<*>) {
            classpath(platform(securityBom!!))
        }
    }
}

subprojects {

    // Only applies the following configuration if the project has the "node-gradle" plugin defined and enabled.
    plugins.withId(rootProject.libs.plugins.node.gradle.get().pluginId) {

        // Configure the Node plugin
        extensions.configure<NodeExtension> {
            version.set(rootProject.libs.versions.node)
            download.set(System.getenv("CI").toBoolean())
        }

        val rsbuildClean = tasks.register<Delete>("rsbuildClean") {
            description = "Clean up just the rsbuild output"
            delete("build")
        }

        val npmClean = tasks.register<Delete>("npmClean") {
            description = "Clean up the node directories"
            dependsOn(rsbuildClean)
            delete("node_modules", "build", "dist", ".parcel-cache")
        }

        val npmTest = tasks.register<NpmTask>("npmTest") {
            description = "Run tests using npm"
            dependsOn(tasks.named("npmInstall"))

            args.set(listOf("run", "test"))

            inputs.dir("src")
            inputs.dir("node_modules")
            inputs.files("tsconfig.json", "package.json", "jest.config.js")

            outputs.upToDateWhen { true }
        }

        val npmBuild = tasks.register<NpmTask>("npmBuild") {
            description = "Build the React bundle using npm"
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
            description = "Clean up the React bundle"
            dependsOn(npmClean)
        }

        tasks.register<Task>("test") {
            description = "Test the React bundle"
            dependsOn(npmTest)
        }

        tasks.register<Task>("build") {
            description = "Build the React bundle"
            dependsOn(npmBuild)
        }
    }
}
