import fr.il_totore.manadrop.MinecraftDependencyHelper
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
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://libraries.minecraft.net")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly(MinecraftDependencyHelper.spigot("1.8.8"))
    compileOnly(project(":tilapia-api"))
    compileOnly(project(":tilapia-spigot-common"))
    compileOnly("com.comphenix.protocol:ProtocolLib:4.8.0")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}