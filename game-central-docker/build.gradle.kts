import java.io.ByteArrayOutputStream


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
    logging.captureStandardError(LogLevel.ERROR)
    logging.captureStandardOutput(LogLevel.INFO)
}
tasks.docker {
    environment
    dependsOn(":tilapia-game-central:shadowJar")
    doFirst {
        val dockerFile = File(buildDir, "docker")
        copy {
            from(((project(":tilapia-game-central").tasks.getByName("shadowJar") as Zip).archiveFile.get().asFile))
            rename { "server.jar" }
            into(dockerFile)
        }
    }
}



val serverJar = File(projectDir, "server.jar")

docker {
    name = "tilapia-game-central:${getRev()}"
    tag("latest", "tilapia-game-central:latest")
    tag("snapshot", "tilapia-game-central:snapshot")
    tag("remoteSnapshot", "docker.tilapiamc.net:443/repository/docker/tilapia-game-central:snapshot")
    pull(true)
    noCache(false)
    this.files("src/")
    this.files("Tilapia-Game-Central-Docker/")
    this.files(serverJar)


}
