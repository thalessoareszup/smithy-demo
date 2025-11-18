description = "Incident hub implementation"

plugins {
    java
    application
    kotlin("jvm").version("2.2.21")
}

dependencies {
    val serviceDefinitionsVersion: String by project
    val smithyJavaVersion: String by project

    implementation("com.stackspot.labs:service-definitions:$serviceDefinitionsVersion")
    implementation ("software.amazon.smithy.java:server-netty:$smithyJavaVersion")
}

application {
    mainClass.set("com.stackspot.labs.incidenthub.MainKt")
}

tasks.named<Jar>("jar") {
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
    from({
        configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it else zipTree(it)
        }
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

}

tasks.named<Tar>("distTar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.named<Zip>("distZip") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.named<Sync>("installDist") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}