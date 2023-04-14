import fr.il_totore.manadrop.MinecraftDependencyHelper
import fr.il_totore.manadrop.MinecraftRepositoryHelper

val ktor_version: String by project
val kotlin_version: String by project
val exposed_version: String by project
val h2_version: String by project

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
    maven("https://jitpack.io")
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

    implementation("org.ow2.asm:asm:9.4")
    implementation("org.ow2.asm:asm-commons:9.4")
    implementation("org.ow2.asm:asm-util:9.4")
    implementation("com.github.fan87:Regular-Bytecode-Expression:2.1.1")
    implementation(kotlin("reflect"))

    api("org.jetbrains.exposed:exposed-core:$exposed_version")
    api("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    api("com.h2database:h2:$h2_version")
    api("com.mysql:mysql-connector-j:8.0.32")
}
