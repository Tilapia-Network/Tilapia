plugins {
    kotlin("jvm")
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
    compileOnly(project(":tilapia-proxy-api"))
}

tasks.shadowJar {
    dependencies {
        dependencyFilter.exclude { "kotlin" in it.moduleGroup }
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}