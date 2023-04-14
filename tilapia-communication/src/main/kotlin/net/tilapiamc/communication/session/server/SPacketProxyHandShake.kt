package net.tilapiamc.communication.session.server

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.tilapiamc.communication.session.SessionPacket
import net.tilapiamc.common.json.*
import java.util.UUID

class SPacketProxyHandShake(
    var proxyId: UUID,
): SessionPacket.SPacket() {

    constructor(): this(UUID.randomUUID())

    override fun toJson(gson: Gson): JsonObject {
        return gson.jsonObjectOf(
            "proxyId" to proxyId,
        )
    }

    override fun fromJson(gson: Gson, jsonObject: JsonObject) {
        this.proxyId = jsonObject[gson, "proxyId"]!!
    }

}