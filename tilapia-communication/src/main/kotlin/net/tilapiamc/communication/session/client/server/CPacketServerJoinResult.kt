package net.tilapiamc.communication.session.client.server

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.tilapiamc.common.json.get
import net.tilapiamc.common.json.jsonObjectOf
import net.tilapiamc.communication.JoinResult
import net.tilapiamc.communication.session.SessionPacket

class CPacketServerJoinResult(
    var transmissionId: Long,
    var joinResult: JoinResult,
): SessionPacket.CPacket() {

    constructor(): this(0L, JoinResult(true, 0.0, " "))

    override fun toJson(gson: Gson): JsonObject {
        return gson.jsonObjectOf(
            "transmissionId" to transmissionId,
            "joinResult" to joinResult,
        )
    }

    override fun fromJson(gson: Gson, jsonObject: JsonObject) {
        this.transmissionId = jsonObject[gson, "transmissionId"]!!
        this.joinResult = jsonObject[gson, "joinResult"]!!
    }

}