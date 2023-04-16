package net.tilapiamc.communication.session.server.server

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.tilapiamc.common.json.get
import net.tilapiamc.common.json.jsonObjectOf
import net.tilapiamc.communication.PlayerInfo
import net.tilapiamc.communication.session.SessionPacket
import java.util.*

class SPacketServerRequestJoinResult(
    var transmissionId: Long,
    var player: PlayerInfo,
    var gameId: UUID,
    var forceJoin: Boolean
): SessionPacket.SPacket() {

    constructor(): this(0L, PlayerInfo("", UUID.randomUUID(), Locale.TRADITIONAL_CHINESE, UUID.randomUUID()), UUID.randomUUID(), false)

    override fun toJson(gson: Gson): JsonObject {
        return gson.jsonObjectOf(
            "transmissionId" to transmissionId,
            "player" to player,
            "gameId" to gameId,
            "forceJoin" to forceJoin,
        )
    }

    override fun fromJson(gson: Gson, jsonObject: JsonObject) {
        this.transmissionId = jsonObject[gson, "transmissionId"]!!
        this.player = jsonObject[gson, "player"]!!
        this.gameId = jsonObject[gson, "gameId"]!!
        this.forceJoin = jsonObject[gson, "forceJoin"]!!
    }

}