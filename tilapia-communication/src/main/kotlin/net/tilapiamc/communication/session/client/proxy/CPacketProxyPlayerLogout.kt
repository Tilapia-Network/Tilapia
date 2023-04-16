package net.tilapiamc.communication.session.client.proxy

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.tilapiamc.common.json.get
import net.tilapiamc.common.json.jsonObjectOf
import net.tilapiamc.communication.session.SessionPacket
import java.util.*

class CPacketProxyPlayerLogout(
    var playerUUID: UUID,
): SessionPacket.CPacket() {

    constructor(): this(UUID.randomUUID())

    override fun toJson(gson: Gson): JsonObject {
        return gson.jsonObjectOf(
            "playerUUID" to playerUUID,
        )
    }

    override fun fromJson(gson: Gson, jsonObject: JsonObject) {
        this.playerUUID = jsonObject[gson, "playerUUID"]!!
    }

}