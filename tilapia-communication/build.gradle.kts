
val ktor_version: String by project
val kotlin_version: String by project
val exposed_version: String by project
val h2_version: String by project

plugins {
    kotlin("jvm") version "1.8.20"
}

group = "net.tilapiamc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api("com.google.code.gson:gson:2.10.1")
    api("io.ktor:ktor-client-core:$ktor_version")
    api("io.ktor:ktor-client-websockets-jvm:$ktor_version")
    api("io.ktor:ktor-client-okhttp:$ktor_version")
    api("io.ktor:ktor-client-content-negotiation:$ktor_version")
    api("io.ktor:ktor-client-auth-jvm:$ktor_version")
    api("io.ktor:ktor-serialization-gson:$ktor_version")

    api("org.jetbrains.exposed:exposed-core:$exposed_version")
    api("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    api("com.h2database:h2:$h2_version")
    api("com.mysql:mysql-connector-j:8.0.32")
    implementation(project(":tilapia-common"))
}
