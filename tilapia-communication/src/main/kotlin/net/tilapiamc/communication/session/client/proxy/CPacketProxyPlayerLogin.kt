package net.tilapiamc.communication.session.client.proxy

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.tilapiamc.common.json.get
import net.tilapiamc.common.json.jsonObjectOf
import net.tilapiamc.communication.PlayerInfo
import net.tilapiamc.communication.session.SessionPacket
import java.util.*

class CPacketProxyPlayerLogin(
    var playerInfo: PlayerInfo
): SessionPacket.CPacket() {

    constructor(): this(PlayerInfo("", UUID.randomUUID(), Locale.TRADITIONAL_CHINESE, UUID.randomUUID()))

    override fun toJson(gson: Gson): JsonObject {
        return gson.jsonObjectOf(
            "playerInfo" to playerInfo,
        )
    }

    override fun fromJson(gson: Gson, jsonObject: JsonObject) {
        this.playerInfo = jsonObject[gson, "playerInfo"]!!
    }

}