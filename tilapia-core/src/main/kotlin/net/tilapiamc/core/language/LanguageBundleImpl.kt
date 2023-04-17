package net.tilapiamc.core.language

import net.tilapiamc.common.language.LanguageBundle
import net.tilapiamc.common.language.LanguageKey

class LanguageBundleImpl(val map: HashMap<String, String>): LanguageBundle {


    override fun get(key: LanguageKey): String {
        LanguageManagerImpl.verifyLanguageKeyRegistration(key)
        return map[key.name]?:key.defaultValue
    }

}