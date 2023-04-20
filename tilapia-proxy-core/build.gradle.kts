plugins {
    kotlin("jvm") version "1.8.0"
    kotlin("kapt") version "1.8.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "net.tilapiamc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.1.1")
    kapt("com.velocitypowered:velocity-api:3.1.1")
    implementation(project(":tilapia-proxy-api"))
    implementation(project(":tilapia-communication"))
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}