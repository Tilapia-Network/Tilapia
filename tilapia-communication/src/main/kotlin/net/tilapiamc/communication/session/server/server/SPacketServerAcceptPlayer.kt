package net.tilapiamc.communication.session.server.server

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.tilapiamc.common.json.get
import net.tilapiamc.common.json.jsonObjectOf
import net.tilapiamc.communication.session.SessionPacket
import java.util.*

class SPacketServerAcceptPlayer(
    var transmissionId: Long,
    var serverId: UUID,
    var gameId: UUID,
    var player: UUID,
    var spectate: Boolean
): SessionPacket.SPacket() {

    constructor(): this(0L, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), false)

    override fun toJson(gson: Gson): JsonObject {
        return gson.jsonObjectOf(
            "transmissionId" to transmissionId,
            "serverId" to serverId,
            "gameId" to gameId,
            "player" to player,
            "spectate" to spectate,
        )
    }

    override fun fromJson(gson: Gson, jsonObject: JsonObject) {
        this.transmissionId = jsonObject[gson, "transmissionId"]!!
        this.serverId = jsonObject[gson, "serverId"]!!
        this.gameId = jsonObject[gson, "gameId"]!!
        this.player = jsonObject[gson, "player"]!!
        this.spectate = jsonObject[gson, "spectate"]!!
    }

}