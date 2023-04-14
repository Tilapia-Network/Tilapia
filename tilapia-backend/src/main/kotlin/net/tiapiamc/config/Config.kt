package net.tiapiamc.config

import java.io.File
import java.io.FileReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.Properties
import kotlin.reflect.KProperty



object Config {

    private val configFile = File("tilapia-backend.properties")
    private val configData = Properties().also {
        it.load(InputStreamReader(configFile.inputStream(), StandardCharsets.UTF_8))
    }

    val HOST by ConfigValue("127.0.0.1") { it }
    val PORT by ConfigValue(8080) { it.toInt() }
    val API_KEY by ConfigValue("testKey") { it }
    val DATABASE_URL by ConfigValue("http://localhost:8080") { it }
    val DATABASE_USER by ConfigValue("root") { it }
    val DATABASE_PASSWORD by ConfigValue("password") { it }



    private class ConfigValue<T>(val defaultValue: T? = null, val name: String? = null, val converter: (String) -> T ) {

        operator fun provideDelegate(thisRef: Any, property: KProperty<*>): ConfigValue<T> {
            println("Config: $name")
            return ConfigValue(defaultValue, property.name, converter)
        }


        operator fun getValue(config: Config, property: KProperty<*>): T {
            val string = System.getenv(property.name)?:configData.getProperty(property.name.lowercase().replace("_", "-"))
            return string?.let(converter)?:defaultValue?: error("Invalid config: Config value ${property.name} is not found!")
        }

    }

}
