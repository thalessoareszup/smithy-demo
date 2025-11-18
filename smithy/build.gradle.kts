import com.github.gradle.node.npm.task.NpmTask

description = "Smithy definitions"

plugins {
    `java-library`
    id("software.amazon.smithy.gradle.smithy-base")
    id("maven-publish")
    id("com.github.node-gradle.node")
}

dependencies {
    val smithyVersion: String by project
    val smithyJavaVersion: String by project
    val smithyTypescriptVersion: String by project

    api("software.amazon.smithy:smithy-aws-traits:$smithyVersion")
    implementation("software.amazon.smithy:smithy-model:$smithyJavaVersion")
    implementation ("software.amazon.smithy.java:aws-server-restjson:$smithyJavaVersion")
    implementation("software.amazon.smithy.java.codegen:plugins:$smithyJavaVersion")

    smithyBuild("software.amazon.smithy.typescript:smithy-aws-typescript-codegen:$smithyTypescriptVersion")
    smithyBuild("software.amazon.smithy.java.codegen:plugins:$smithyJavaVersion")
}

java.sourceSets["main"].java {
    srcDirs("model", "build/smithyprojections/smithy/source/java-server-codegen")
}

// Publish the generated java classes to local repository
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "com.stackspot.labs"
            artifactId = "service-definitions" // fixed typo
            version = "0.0.1"
        }
    }
    repositories {
        mavenLocal()
    }
}

// Build the smithy classes before compiling the generated java code
tasks.named("compileJava") {
    dependsOn("smithyBuild")
}

// Typescript generated package requires yarn to run the build concurrently
// Defining each task and installing after generating so it's readily available
// for client consumers.
val typescriptCodegenDir = "$buildDir/smithyprojections/smithy/source/typescript-codegen"

tasks.register<NpmTask>("npmInstallCodegen") {
    workingDir.set(file(typescriptCodegenDir))
    args.set(listOf("install"))
}

tasks.register<NpmTask>("buildTsCjs") {
    dependsOn("npmInstallCodegen")
    workingDir.set(file(typescriptCodegenDir))
    args.set(listOf("run", "build:cjs"))
}

tasks.register<NpmTask>("buildTsEs") {
    dependsOn("npmInstallCodegen")
    workingDir.set(file(typescriptCodegenDir))
    args.set(listOf("run", "build:es"))
}

tasks.register<NpmTask>("buildTsTypes") {
    dependsOn("npmInstallCodegen")
    workingDir.set(file(typescriptCodegenDir))
    args.set(listOf("run", "build:types"))
}

tasks.register("buildAllTs") {
    dependsOn("buildTsCjs", "buildTsEs", "buildTsTypes")
}

tasks.named("build") {
    dependsOn("buildAllTs")
}