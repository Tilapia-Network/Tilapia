package net.tilapiamc.communication.session

import com.google.gson.Gson
import com.google.gson.JsonObject

abstract class SessionPacket {

    abstract fun toJson(gson: Gson): JsonObject
    abstract fun fromJson(gson: Gson, jsonObject: JsonObject)

    abstract class SPacket: SessionPacket()
    abstract class CPacket: SessionPacket()
}
