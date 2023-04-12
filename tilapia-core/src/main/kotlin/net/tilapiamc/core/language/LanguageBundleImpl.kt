package net.tilapiamc.core.language

import net.tilapiamc.api.language.LanguageBundle
import net.tilapiamc.api.language.LanguageKey
import org.apache.logging.log4j.LogManager

class LanguageBundleImpl(val map: HashMap<String, String>): LanguageBundle {


    override fun get(key: LanguageKey): String {
        LanguageManagerImpl.verifyLanguageKeyRegistration(key)
        return map[key.name]?:key.defaultValue
    }

}