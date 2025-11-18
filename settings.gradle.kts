rootProject.name = "smithy-demo"

pluginManagement {
    val smithyGradleVersion: String by settings
    val nodePluginVersion: String by settings
    plugins {
        id("software.amazon.smithy.gradle.smithy-jar").version(smithyGradleVersion)
        id("software.amazon.smithy.gradle.smithy-base").version(smithyGradleVersion)
        id("com.github.node-gradle.node").version(nodePluginVersion).apply(false)
    }

    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

// Subprojects
include("smithy")
include("incident-hub")