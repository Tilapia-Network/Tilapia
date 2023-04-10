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
    implementation(project(":tilapia-command"))
    implementation(project(":fan87-plugin-dev-kit"))
    implementation(project(":tilapia-api"))
    compileOnly("org.apache.logging.log4j:log4j-core:2.20.0")
}


spigot {
    desc {
        named(project.name)
        version(project.version.toString())
        main("net.tilapiamc.core.main.Main")
    }
}

tasks.classes.get().dependsOn("spigotPlugin")