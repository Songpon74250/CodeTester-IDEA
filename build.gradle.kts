import net.ltgt.gradle.errorprone.errorprone
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    // Java
    id("java")

    // Kotlin
    id("org.jetbrains.kotlin.jvm") version "1.5.21" apply false

    // IntelliJ Gradle Plugin
    id("org.jetbrains.intellij") version "1.1.4"

    // IntelliJ Changelog Plugin
    id("org.jetbrains.changelog") version "1.2.0" apply false

    // Google Error Prone
    id("net.ltgt.errorprone") version "2.0.2"
}


allprojects {
    group = properties("pluginGroup")
    version = properties("pluginVersion")

    repositories {
        mavenCentral()

        maven {
            url = URI("https://jetbrains.bintray.com/intellij-third-party-dependencies")
        }

        maven {
            url = URI("https://packages.jetbrains.team/maven/p/ij/intellij-dependencies")
        }
    }
}

subprojects {

    apply {
        plugin("java")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.intellij")
        plugin("net.ltgt.errorprone")
    }

    if (project.name != "CodeTester-IntelliJ-IDEA") {
        apply {
            dependencies {
                implementation(project(":CodeTester-IntelliJ-IDEA"))
            }
        }
    }

    apply {
        dependencies {
            implementation(
                group = "org.projectlombok",
                name = "lombok",
                version = properties("dependency-lombok")
            )
            annotationProcessor(
                group = "org.projectlombok",
                name = "lombok",
                version = properties("dependency-lombok")
            )

            testImplementation(
                group = "org.junit.jupiter",
                name = "junit-jupiter",
                version = properties("dependency-junit-jupiter")
            )

            errorprone(
                group = "com.google.errorprone",
                name = "error_prone_core",
                version = properties("dependency-error_prone_core")
            )
        }
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
            sourceCompatibility = properties("javaVersion")
            targetCompatibility = properties("javaVersion")

            options.errorprone.disableWarningsInGeneratedCode.set(true)
        }

        withType<KotlinCompile> {
            kotlinOptions.jvmTarget = properties("javaVersion")
        }
    }

    intellij {
        pluginName.set(properties("pluginName"))
        type.set(properties("platformType"))
        version.set(properties("platformVersion"))

        downloadSources.set(properties("platformDownloadSources").toBoolean())
        updateSinceUntilBuild.set(true)

        plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))
    }
}


