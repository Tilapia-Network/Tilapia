plugins {
}

group = "net.tilapiamc"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        maven("https://repo.dmulloy2.net/repository/public/")
        maven("https://libraries.minecraft.net")
        maven("https://jitpack.io")
    }
}

repositories {
    mavenCentral()
}

dependencies {
}