import java.io.ByteArrayOutputStream

val SPIGOT_PLUGINS = arrayOf(
    "tilapia-core",
    "tilapia-fleetwars",
    "tilapia-lobby",
    "tilapia-multiworld",
    "tilapia-sandbox",
    "tilapia-util-commands",
    "tilapia-auto-op", // TODO: Remvoe on production
    "tilapia-client-integration",
)

plugins {
    id("com.palantir.docker") version "0.35.0"
    id("java")
}

group = "net.tilapiamc"
version = "1.0-SNAPSHOT"

repositories {
}

dependencies {
}


fun getDependency(notation: String): File {
    val dependency = dependencyFactory.create(notation)
    configurations.implementation.get().dependencies.add(dependency)
    return configurations.runtimeClasspath.get().files(dependency).first()
}

fun getSpigotJar(version: String): File {
    return getDependency("org.spigotmc:spigot:$version-R0.1-SNAPSHOT")
}

fun getRev(): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-parse", "HEAD")
        standardOutput = byteArrayOutputStream
    }
    return byteArrayOutputStream.toString().trim()
}

task<Exec>("testTask") {
    workingDir(File(buildDir, "docker"))
    commandLine("docker", "--help")
    logging.captureStandardError(org.gradle.api.logging.LogLevel.ERROR)
    logging.captureStandardOutput(org.gradle.api.logging.LogLevel.INFO)
}
tasks.docker {
    environment
    for (projectName in SPIGOT_PLUGINS) {
        dependsOn(":${projectName}:shadowJar")
    }
    doFirst {
        val dockerFile = File(buildDir, "docker")
        for (projectName in SPIGOT_PLUGINS) {
            copy {
                from(((project(":$projectName").tasks.getByName("shadowJar") as Zip).archiveFile.get().asFile))
                into(File(dockerFile, "plugins"))
            }
        }
    }
}

val spigotJar = getSpigotJar("1.8.8")

docker {
    name = "tilapia-spigot:${getRev()}"
    tag("latest", "tilapia-spigot:latest")
    tag("snapshot", "tilapia-spigot:snapshot")
    tag("remoteSnapshot", "docker.tilapiamc.net:443/repository/docker/tilapia-spigot:snapshot")
    this.buildArgs(mapOf(
        "SPIGOT_JAR" to spigotJar.name
    ))
    pull(true)
    noCache(false)
    this.files("src/")
    this.files("Tilapia-Spigot-Docker/")
    this.files(spigotJar)


}
