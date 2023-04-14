package net.tilapiamc.communication.session.server

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.tilapiamc.common.json.get
import net.tilapiamc.common.json.jsonObjectOf
import net.tilapiamc.communication.session.SessionPacket
import java.util.*

class SPacketServerHandShake(
    var proxyId: UUID,
    var serverId: UUID
): SessionPacket.SPacket() {

    constructor(): this(UUID.randomUUID(), UUID.randomUUID())

    override fun toJson(gson: Gson): JsonObject {
        return gson.jsonObjectOf(
            "proxyId" to proxyId,
            "serverId" to serverId
        )
    }

    override fun fromJson(gson: Gson, jsonObject: JsonObject) {
        this.proxyId = jsonObject[gson, "proxyId"]!!
        this.serverId = jsonObject[gson, "serverId"]!!
    }

}