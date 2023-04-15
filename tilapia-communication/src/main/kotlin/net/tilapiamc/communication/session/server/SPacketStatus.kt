package net.tilapiamc.communication.session.server

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.tilapiamc.common.json.get
import net.tilapiamc.common.json.jsonObjectOf
import net.tilapiamc.communication.session.SessionPacket

class SPacketStatus(
    var statusCode: Int,
    var transmissionId: Long,
    var message: String
): SessionPacket.SPacket() {

    constructor(): this(0, 0L, "")

    override fun toJson(gson: Gson): JsonObject {
        return gson.jsonObjectOf(
            "statusCode" to statusCode,
            "transmissionId" to transmissionId,
            "message" to message,
        )
    }

    override fun fromJson(gson: Gson, jsonObject: JsonObject) {
        this.statusCode = jsonObject[gson, "statusCode"]!!
        this.transmissionId = jsonObject[gson, "transmissionId"]!!
        this.message = jsonObject[gson, "message"]!!
    }

}