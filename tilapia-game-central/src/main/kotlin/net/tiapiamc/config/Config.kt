package net.tiapiamc.config

import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.reflect.KProperty


private val logger = LoggerFactory.getLogger("Configurator")

object Config {

    private val configFile = File("tilapia-backend.properties").apply { createNewFile() }
    private val configData = Properties().also {
        it.load(InputStreamReader(configFile.inputStream(), StandardCharsets.UTF_8))
    }

    val HOST by ConfigValue("127.0.0.1") { it }
    val PORT by ConfigValue("8080") { it.toInt() }
    val API_KEY by ConfigValue("testKey") { it }
    val DATABASE_URL by ConfigValue("jdbc:mysql://localhost:3306") { it }
    val DATABASE_USER by ConfigValue("root") { it }
    val DATABASE_PASSWORD by ConfigValue("password") { it }



    private class ConfigValue<T>(val defaultValue: String? = null, val converter: (String) -> T ) {

        operator fun provideDelegate(thisRef: Any, property: KProperty<*>): ConfigValue<T> {
            if (!configData.containsKey(property.name.lowercase().replace("_", "-"))) {
                configData.setProperty(property.name.lowercase().replace("_", "-"), defaultValue)
                configData.store(OutputStreamWriter(configFile.outputStream(), StandardCharsets.UTF_8), "Tilapia Backend Configuration file")
            }
            logger.debug("Registered config value: ${property.name}")
            return ConfigValue(defaultValue, converter)
        }


        operator fun getValue(config: Config, property: KProperty<*>): T {
            val string = System.getenv(property.name)?:configData.getProperty(property.name.lowercase().replace("_", "-"))?:defaultValue
            return string?.let(converter)?: error("Invalid config: Config value ${property.name} is not found!")
        }

    }

}
