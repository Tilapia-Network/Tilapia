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
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.comphenix.protocol:ProtocolLib:4.8.0")
    compileOnly(MinecraftDependencyHelper.spigot("1.8.8"))
    implementation(project(":fan87-plugin-dev-kit"))
    implementation(project(":tilapia-api"))
    implementation(project(":tilapia-common"))
    implementation(project(":tilapia-language"))
    implementation(project(":tilapia-spigot-common"))

    implementation("org.ow2.asm:asm:9.4")
    implementation("org.ow2.asm:asm-commons:9.4")
    implementation("org.ow2.asm:asm-util:9.4")
    implementation("com.github.fan87:Regular-Bytecode-Expression:2.1.1")
    implementation("com.github.fan87.Java-Injector:native-agent:master-SNAPSHOT")
}
