package net.tilapiamc.core.language

import net.tilapiamc.api.language.LanguageBundle
import net.tilapiamc.api.language.LanguageKey

class LanguageBundleImpl(val map: HashMap<String, String>): LanguageBundle {

    override fun get(key: LanguageKey): String {
        return map[key.name]?:key.defaultValue
    }

}