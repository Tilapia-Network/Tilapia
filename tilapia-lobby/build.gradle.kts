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
    implementation(project(":tilapia-api"))
    implementation(project(":tilapia-common"))
    implementation(project(":tilapia-spigot-common"))
}

spigot {
    desc {
        named(project.name)
        version(project.version.toString())
        main("net.tilapia.lobby.main.Main")
    }
}

tasks.classes.get().dependsOn("spigotPlugin")
