import fr.il_totore.manadrop.MinecraftDependencyHelper
import fr.il_totore.manadrop.MinecraftRepositoryHelper

plugins {
    kotlin("jvm") version "1.8.0"
    id("fr.il_totore.manadrop") version "0.4.3"
}

group = rootProject.group
version = "1.0-SNAPSHOT"

repositories {
    MinecraftRepositoryHelper.spigotSnapshot()
    MinecraftRepositoryHelper.sonatype()
    mavenCentral()
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://libraries.minecraft.net")
}

dependencies {
    compileOnly("com.comphenix.protocol:ProtocolLib:4.8.0")
    implementation(project(":fan87-plugin-dev-kit"))
    implementation(project(":tilapia-command"))
    implementation("org.reflections:reflections:0.10.2")
    compileOnly(MinecraftDependencyHelper.spigotApi("1.8.8"))
    compileOnly("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("com.mojang:brigadier:1.0.18")
    implementation("io.netty:netty-all:4.0.23.Final")

}
