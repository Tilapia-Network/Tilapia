package net.tilapiamc.common.language

import kotlin.reflect.KProperty

interface LanguageBundle {

    operator fun get(key: LanguageKey): String

}

data class LanguageKey(val name: String, val defaultValue: String)

fun languageKey(name: String, defaultValue: String): LanguageKey {
    return LanguageKey(name, defaultValue)
}

class LanguageKeyDelegation(val prefix: String, val defaultValue: String?) {

    constructor(defaultValue: String?): this("", defaultValue)
    operator fun provideDelegate(owner: Any?, property: KProperty<*>): LanguageKeyDelegation {
        LanguageManager.instance.registerLanguageKey(LanguageKey(prefix + property.name, defaultValue?:(prefix + property.name)))
        return this
    }
    operator fun getValue(owner: Any?, property: KProperty<*>): LanguageKey {
        return LanguageKey(prefix + property.name, defaultValue?:(prefix + property.name))
    }
}