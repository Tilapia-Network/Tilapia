package net.tilapiamc.common.language

import kotlin.reflect.KProperty

interface LanguageBundle {

    operator fun get(key: LanguageKey): String

}

data class LanguageKey(val name: String, val defaultValue: String)

fun languageKey(name: String, defaultValue: String): LanguageKey {
    return LanguageKey(name, defaultValue)
}

class LanguageKeyDelegation(val defaultValue: String? = null) {
    operator fun provideDelegate(owner: Any?, property: KProperty<*>): LanguageKeyDelegation {
        LanguageManager.instance.registerLanguageKey(LanguageKey(property.name, defaultValue?:property.name))
        return this
    }
    operator fun getValue(owner: Any?, property: KProperty<*>): LanguageKey {
        return LanguageKey(property.name, defaultValue?:property.name)
    }
}