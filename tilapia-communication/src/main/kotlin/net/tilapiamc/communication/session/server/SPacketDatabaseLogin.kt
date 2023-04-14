package net.tilapiamc.communication.session.server

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.tilapiamc.common.json.get
import net.tilapiamc.common.json.jsonObjectOf
import net.tilapiamc.communication.DatabaseLogin
import net.tilapiamc.communication.session.SessionPacket
import java.util.*

class SPacketDatabaseLogin(
    var databaseLogin: DatabaseLogin,
): SessionPacket.SPacket() {

    constructor(): this(DatabaseLogin(UUID.randomUUID(), "", "", ""))

    override fun toJson(gson: Gson): JsonObject {
        return gson.jsonObjectOf(
            "databaseLogin" to databaseLogin,
        )
    }

    override fun fromJson(gson: Gson, jsonObject: JsonObject) {
        this.databaseLogin = jsonObject[gson, "databaseLogin"]!!
    }

}