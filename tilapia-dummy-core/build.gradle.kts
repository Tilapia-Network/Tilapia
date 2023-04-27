import fr.il_totore.manadrop.MinecraftDependencyHelper
import fr.il_totore.manadrop.MinecraftRepositoryHelper

plugins {
    kotlin("jvm")
    id("fr.il_totore.manadrop") version "0.4.3"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}


group = "net.tilapiamc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(MinecraftDependencyHelper.spigotApi("1.8.8"))
    compileOnly(MinecraftDependencyHelper.spigot("1.8.8"))
    implementation(project(":tilapia-command"))
    implementation(project(":fan87-plugin-dev-kit"))
    implementation(project(":tilapia-api"))
    implementation(project(":tilapia-language"))
    compileOnly("org.apache.logging.log4j:log4j-core:2.20.0")
}

spigot {
    desc {
        named("tilapia-core")
        version(project.version.toString())
        main("net.tilapiamc.dummycore.main.Main")
        depend("Citizens")
    }
}

tasks.classes.get().dependsOn("spigotPlugin")


kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}