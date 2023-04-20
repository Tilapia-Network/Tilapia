plugins {
    id("fr.il_totore.manadrop") version "0.4.3"
    id("com.palantir.docker") version "0.35.0"
}

group = "net.tilapiamc"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://repo.dmulloy2.net/repository/public/")
        maven("https://libraries.minecraft.net")
        maven("https://jitpack.io")
        maven("https://maven.citizensnpcs.co/repo")
        maven("https://libraries.minecraft.net")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

repositories {
    mavenCentral()
}

dependencies {
}

tasks.buildTools {
    workDir = File("run/BuildTools")
    workDir.mkdirs()
    allVersions().clear()
    allVersions().add("1.8.8")
    mavenPath = "/usr/bin/mvn"
}