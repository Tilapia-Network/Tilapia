package net.tilapiamc.communication.session.server.proxy

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.tilapiamc.common.json.get
import net.tilapiamc.common.json.jsonObjectOf
import net.tilapiamc.communication.session.SessionPacket
import java.util.*

class SPacketProxyAcceptPlayer(
    var transmissionId: Long,
    var serverId: UUID,
    var player: UUID
): SessionPacket.SPacket() {

    constructor(): this(0L, UUID.randomUUID(), UUID.randomUUID())

    override fun toJson(gson: Gson): JsonObject {
        return gson.jsonObjectOf(
            "transmissionId" to transmissionId,
            "serverId" to serverId,
            "player" to player,
        )
    }

    override fun fromJson(gson: Gson, jsonObject: JsonObject) {
        this.transmissionId = jsonObject[gson, "transmissionId"]!!
        this.serverId = jsonObject[gson, "serverId"]!!
        this.player = jsonObject[gson, "player"]!!
    }

}