package net.tilapiamc.common

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import java.io.File
import java.io.FileReader
import kotlin.reflect.KProperty

open class Config(val configFile: File, val gson: Gson = GsonBuilder().setPrettyPrinting().create()) {

    val configProperties = ArrayList<ConfigProperty<*>>()
    lateinit var dataObject: JsonObject

    init {
        configFile.parentFile.mkdirs()
        configFile.createNewFile()
        reload()
    }

    fun reload() {
        dataObject = getConfigObject()?:JsonObject()
        for (configProperty in configProperties) {
            if (!dataObject.has(configProperty.name)) {
                dataObject.add(configProperty.name, gson.toJsonTree(configProperty.defaultValue))
            }
        }
        save()
    }

    fun save() {
        configFile.parentFile.mkdirs()
        configFile.createNewFile()
        configFile.writeText(gson.toJson(dataObject))
    }

    private fun getConfigObject(): JsonObject? {
        configFile.parentFile.mkdirs()
        configFile.createNewFile()
        return gson.fromJson(FileReader(configFile), JsonObject::class.java)
    }

}

class ConfigProperty<T>(val name: String, val type: Class<T>, val defaultValue: T)


class ConfigDelegationProvider<T>(val valueType: Class<T>, val defaultValue: T) {
    operator fun provideDelegate(thisRef: Config, property: KProperty<*>): ConfigDelegationProvider<T> {
        thisRef.configProperties.add(ConfigProperty(property.name, valueType, defaultValue))
        return this
    }

    operator fun getValue(thisRef: Config, property: KProperty<*>): T {
        if (thisRef.configProperties.firstOrNull { it.name == property.name } == null) {
            throw IllegalStateException("Config value ${property.name} is not registered")
        }
        return thisRef.gson.fromJson(thisRef.dataObject.get(property.name), valueType)?:defaultValue
    }
}

inline fun <reified T> configValue(defaultValue: T): ConfigDelegationProvider<T> {
    return ConfigDelegationProvider(T::class.java, defaultValue)
}