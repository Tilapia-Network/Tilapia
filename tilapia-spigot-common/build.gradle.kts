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
}

dependencies {
    compileOnly("com.comphenix.protocol:ProtocolLib:4.8.0")
    compileOnly(MinecraftDependencyHelper.spigotApi("1.8.8"))
    compileOnly(project(":tilapia-api"))
    compileOnly("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation(project(":tilapia-common"))
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}