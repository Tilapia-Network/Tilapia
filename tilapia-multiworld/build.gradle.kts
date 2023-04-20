import fr.il_totore.manadrop.MinecraftDependencyHelper
import fr.il_totore.manadrop.MinecraftRepositoryHelper

plugins {
    kotlin("jvm") version "1.8.0"
    id("fr.il_totore.manadrop") version "0.4.3"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = rootProject.group
version = "1.0-SNAPSHOT"

repositories {
    MinecraftRepositoryHelper.spigotSnapshot()
    MinecraftRepositoryHelper.sonatype()
    mavenCentral()
}

dependencies {
    compileOnly(MinecraftDependencyHelper.spigotApi("1.8.8"))
    compileOnly(project(":tilapia-api"))
    implementation(project(":tilapia-common"))
    implementation(project(":tilapia-spigot-common"))
    implementation(project(":tilapia-command"))
    compileOnly("org.apache.logging.log4j:log4j-core:2.20.0")
}

spigot {
    desc {
        named(project.name)
        version(project.version.toString())
        main("net.tilapiamc.multiworld.MultiWorld")
        depend("tilapia-core")
    }
}

tasks.classes.get().dependsOn("spigotPlugin")

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}