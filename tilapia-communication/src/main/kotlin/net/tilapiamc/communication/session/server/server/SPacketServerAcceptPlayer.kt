package net.tilapiamc.communication.session.server.server

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.tilapiamc.common.json.get
import net.tilapiamc.common.json.jsonObjectOf
import net.tilapiamc.communication.session.SessionPacket
import java.util.*

class SPacketServerAcceptPlayer(
    var transmissionId: Long,
    var gameId: UUID,
    var player: UUID
): SessionPacket.SPacket() {

    constructor(): this(0L, UUID.randomUUID(), UUID.randomUUID())

    override fun toJson(gson: Gson): JsonObject {
        return gson.jsonObjectOf(
            "transmissionId" to transmissionId,
            "gameId" to gameId,
            "player" to gameId,
        )
    }

    override fun fromJson(gson: Gson, jsonObject: JsonObject) {
        this.transmissionId = jsonObject[gson, "transmissionId"]!!
        this.gameId = jsonObject[gson, "gameId"]!!
        this.player = jsonObject[gson, "player"]!!
    }

}