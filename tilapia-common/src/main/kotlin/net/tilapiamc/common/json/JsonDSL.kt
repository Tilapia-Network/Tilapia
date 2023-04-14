package net.tilapiamc.common.json

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

operator fun JsonElement.set(key: String, value: Number) {
    this.asJsonObject.addProperty(key, value)
}
operator fun JsonElement.set(key: String, value: Char) {
    this.asJsonObject.addProperty(key, value)
}
operator fun JsonElement.set(key: String, value: String) {
    this.asJsonObject.addProperty(key, value)
}
operator fun JsonElement.set(key: String, value: Boolean) {
    this.asJsonObject.addProperty(key, value)
}
operator fun JsonElement.set(key: String, value: JsonElement) {
    this.asJsonObject.add(key, value)
}
operator fun JsonObject.set(key: String, value: Number) {
    this.addProperty(key, value)
}
operator fun JsonObject.set(key: String, value: Char) {
    this.addProperty(key, value)
}
operator fun JsonObject.set(key: String, value: String) {
    this.addProperty(key, value)
}
operator fun JsonObject.set(key: String, value: Boolean) {
    this.addProperty(key, value)
}
operator fun JsonObject.set(key: String, value: JsonElement) {
    this.add(key, value)
}


inline operator fun <reified T> JsonObject.get(gson: Gson = Gson(), key: String): T? {
    return if (this.isJsonNull) return null else gson.fromJson(this[key], T::class.java)
}

fun Gson.jsonObjectOf(vararg content: Pair<String, Any>): JsonObject {
    val out = JsonObject()
    for (pair in content) {
        out.add(pair.first, toJsonTree(pair.second))
    }
    return out
}
fun Gson.jsonArrayOf(vararg entries: Any): JsonArray {
    val out = JsonArray()
    for (entry in entries) {
        out.add(toJsonTree(entry))
    }
    return out
}