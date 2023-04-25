import fr.il_totore.manadrop.MinecraftRepositoryHelper

plugins {
    kotlin("jvm")
    id("fr.il_totore.manadrop") version "0.4.3"
}

group = rootProject.group
version = "1.0-SNAPSHOT"

repositories {
    MinecraftRepositoryHelper.spigotSnapshot()
    MinecraftRepositoryHelper.sonatype()
    mavenCentral()
}

dependencies {
    compileOnly("org.apache.logging.log4j:log4j-core:2.20.0")
    api("com.mojang:brigadier:1.0.18")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}