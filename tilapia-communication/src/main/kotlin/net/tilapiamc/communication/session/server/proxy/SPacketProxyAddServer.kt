package net.tilapiamc.communication.session.server.proxy

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.tilapiamc.common.json.get
import net.tilapiamc.common.json.jsonObjectOf
import net.tilapiamc.communication.ServerInfo
import net.tilapiamc.communication.session.SessionPacket
import java.util.*

class SPacketProxyAddServer(
    var server: ServerInfo,
): SessionPacket.SPacket() {

    constructor(): this(ServerInfo(UUID.randomUUID(), UUID.randomUUID(), arrayListOf()))

    override fun toJson(gson: Gson): JsonObject {
        return gson.jsonObjectOf(
            "server" to server,
        )
    }

    override fun fromJson(gson: Gson, jsonObject: JsonObject) {
        this.server = jsonObject[gson, "server"]!!
    }

}