package net.tiapiamc.config

import kotlin.reflect.KProperty

object Config {

    val HOST by Env { it }
    val PORT by Env { it.toInt() }
    val API_KEY by Env { it }

}

private class Env<T>(val converter: (String) -> T ) {
    operator fun getValue(config: Config, property: KProperty<*>): T {
        return converter(System.getenv(property.name))
    }

}