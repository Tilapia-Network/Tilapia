plugins {
    kotlin("jvm") version "1.8.0"
}

group = "net.tilapiamc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    api(project(":tilapia-common"))
    compileOnly("com.velocitypowered:velocity-api:3.1.1")
    annotationProcessor("com.velocitypowered:velocity-api:3.1.1")
    implementation("org.reflections:reflections:0.10.2")
    api(project(":tilapia-command"))
}
