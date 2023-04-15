package net.tilapiamc.communication.session.client.proxy

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.tilapiamc.common.json.get
import net.tilapiamc.common.json.jsonObjectOf
import net.tilapiamc.communication.session.SessionPacket
import java.util.*

class CPacketProxyPlayerLogout(
    var transmissionId: Long,
    var playerUUID: UUID,
): SessionPacket.CPacket() {

    constructor(): this(0L, UUID.randomUUID())

    override fun toJson(gson: Gson): JsonObject {
        return gson.jsonObjectOf(
            "transmissionId" to transmissionId,
            "playerUUID" to playerUUID,
        )
    }

    override fun fromJson(gson: Gson, jsonObject: JsonObject) {
        this.transmissionId = jsonObject[gson, "transmissionId"]!!
        this.playerUUID = jsonObject[gson, "playerUUID"]!!
    }

}