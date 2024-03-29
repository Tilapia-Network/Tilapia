import fr.il_totore.manadrop.MinecraftRepositoryHelper

plugins {
    kotlin("jvm")
    id("fr.il_totore.manadrop") version "0.4.3"
}

group = "net.tilapiamc"
version = "1.0-SNAPSHOT"


repositories {
    MinecraftRepositoryHelper.spigotSnapshot()
    MinecraftRepositoryHelper.sonatype()
    mavenCentral()
}

dependencies {
    compileOnly("net.md-5:bungeecord-chat:1.8-SNAPSHOT")
    compileOnly(project(":tilapia-api"))
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}