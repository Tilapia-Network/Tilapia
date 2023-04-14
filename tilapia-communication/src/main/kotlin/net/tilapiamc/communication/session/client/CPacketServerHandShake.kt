package net.tilapiamc.communication.session.client

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.tilapiamc.common.json.get
import net.tilapiamc.common.json.jsonObjectOf
import net.tilapiamc.communication.session.SessionPacket

class CPacketServerHandShake(
    var requiredSchemas: List<String>,
): SessionPacket.CPacket() {

    constructor(): this(arrayListOf())

    override fun toJson(gson: Gson): JsonObject {
        return gson.jsonObjectOf(
            "requiredSchemas" to requiredSchemas,
        )
    }

    override fun fromJson(gson: Gson, jsonObject: JsonObject) {
        val requiredSchemas: JsonArray = jsonObject[gson, "requiredSchemas"]!!
        this.requiredSchemas = ArrayList<String>().also {
            for (requiredSchema in requiredSchemas) {
                it.add(requiredSchema.asString)
            }
        }
    }

}