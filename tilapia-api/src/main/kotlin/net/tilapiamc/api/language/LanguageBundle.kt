package net.tilapiamc.api.language

import kotlin.reflect.KProperty

interface LanguageBundle {

    operator fun get(key: LanguageKey): String

}

data class LanguageKey(val name: String, val defaultValue: String)

class LanguageKeyDelegation(val defaultValue: String? = null) {
    operator fun getValue(owner: Any, property: KProperty<*>): LanguageKey {
        return LanguageKey(property.name, defaultValue?:property.name)
    }
}