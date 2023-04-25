val exposed_version: String by project
val h2_version: String by project


plugins {
    kotlin("jvm")
}

group = "net.tilapiamc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    api(project(":tilapia-common"))
    api(project(":tilapia-database"))
    compileOnly("com.velocitypowered:velocity-api:3.1.1")
    annotationProcessor("com.velocitypowered:velocity-api:3.1.1")
    implementation("org.reflections:reflections:0.10.2")
    api(project(":tilapia-command"))

    api("org.jetbrains.exposed:exposed-core:$exposed_version")
    api("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    api("com.h2database:h2:$h2_version")
    api("com.mysql:mysql-connector-j:8.0.32")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}