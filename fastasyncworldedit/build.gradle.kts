plugins {
    kotlin("jvm")
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "net.tilapiamc"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://mvnrepository.com/artifact/")
    mavenCentral()
    maven("https://repo.dmulloy2.net/content/groups/public/")
    maven("https://repo.destroystokyo.com/repository/maven-public//")
    maven("https://ci.athion.net/plugin/repository/tools/")
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.maven.apache.org/maven2")
    maven("https://ci.frostcast.net/plugin/repository/everything")
    maven("https://repo.spongepowered.org/maven")
    maven("https://dl.bintray.com/tastybento/maven-repo")
    maven("https://repo.inventivetalent.org/content/groups/public/")
    maven("https://store.ttyh.ru/libraries/")
    maven("https://repo.dmulloy2.net/nexus/repository/public/")
    maven("https://maven.elmakers.com/repository/")
    maven("https://ci.ender.zone/plugin/repository/everything/")
}

dependencies {
    compileOnly(project(":fan87-plugin-dev-kit"))
    compileOnly(project(":tilapia-client-integration"))
    compileOnly("org.bukkit.craftbukkit:Craftbukkit_1_8:1.8.8")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:6.1.4-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-core:6.1.4-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:4.8.0")
    compileOnly("org.inventivetalent:mapmanager:1.7.2-SNAPSHOT") {
        exclude(group = "*", module = "*")
    }
    compileOnly("org.yaml:snakeyaml:1.16")
    compileOnly("com.google.code.gson:gson:2.2.4")
    implementation("org.primesoft:BlocksHub:2.0")
    implementation("com.github.luben:zstd-jni:1.1.1")
    implementation("co.aikar:fastutil-lite:1.0")
}

tasks.compileJava.get().sourceCompatibility = "1.8"
tasks.compileJava.get().targetCompatibility = "1.8"