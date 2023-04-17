plugins {
    kotlin("jvm") version "1.8.0"
}

group = rootProject.group
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("com.google.code.gson:gson:2.10.1")
    api("org.apache.logging.log4j:log4j-core:2.20.0")

}
