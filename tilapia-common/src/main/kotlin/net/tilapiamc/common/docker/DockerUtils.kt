package net.tilapiamc.common.docker

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import java.io.File

object DockerUtils {
    val gson = GsonBuilder().create()

    fun isInDocker(): Boolean {
        return File("/.dockerenv").exists()
    }

    val containerInfo: JsonObject by lazy {
        val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", "curl -s --unix-socket /var/run/docker.sock http://dummy/containers/${System.getenv("HOSTNAME")}/json"))
        val text = process.inputStream.readBytes().decodeToString()
        println(text)
        gson.fromJson(text, JsonObject::class.java)?:JsonObject()
    }


    fun getContainerGateway(): String {
        return containerInfo["NetworkSettings"]
            .asJsonObject["Networks"]
            .asJsonObject
            .entrySet()
            .first()
            .value
            .asJsonObject["Gateway"]
            .asString
    }
    fun getMinecraftPort(): Int {
        val entry = containerInfo["NetworkSettings"].asJsonObject["Ports"].asJsonObject.entrySet()
        for (value in entry) {
            if (value.key == "25565/tcp") {
                return value.value.asJsonArray.first().asJsonObject["HostPort"].asString.toInt()
            }
        }
        return 25565
    }

}