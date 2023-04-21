import java.io.ByteArrayOutputStream

val PROXY_PLUGINS = arrayOf(
    "tilapia-proxy-core",
    "tilapia-proxy-util-commands",
)

plugins {
    id("com.palantir.docker") version "0.35.0"
    id("java")
}

group = "net.tilapiamc"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
}


fun getDependency(notation: String): File {
    val dependency = dependencyFactory.create(notation)
    configurations.implementation.get().dependencies.add(dependency)
    return configurations.runtimeClasspath.get().files(dependency).first()
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
    for (projectName in PROXY_PLUGINS) {
        dependsOn(":${projectName}:shadowJar")
    }
    doFirst {
        val dockerFile = File(buildDir, "docker")
        for (projectName in PROXY_PLUGINS) {
            copy {
                from(((project(":$projectName").tasks.getByName("shadowJar") as Zip).archiveFile.get().asFile))
                into(File(dockerFile, "plugins"))
            }
        }
    }
}



val serverJar = File(projectDir, "server.jar")

docker {
    name = "tilapia-proxy:${getRev()}"
    tag("latest", "tilapia-proxy:latest")
    tag("snapshot", "tilapia-proxy:snapshot")
    tag("remoteSnapshot", "docker.tilapiamc.net:443/repository/docker/tilapia-proxy:snapshot")
    this.buildArgs(mapOf(
        "PROXY_JAR" to serverJar.name
    ))
    pull(true)
    noCache(false)
    this.files("src/")
    this.files("Tilapia-Proxy-Docker/")
    this.files(serverJar)


}
