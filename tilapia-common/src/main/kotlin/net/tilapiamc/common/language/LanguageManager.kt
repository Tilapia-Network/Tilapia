package net.tilapiamc.common.language

import java.util.*

interface LanguageManager {

    companion object {
        lateinit var instance: LanguageManager
    }

    fun getLanguageBundle(locale: Locale): LanguageBundle
    fun registerLanguageKey(languageKey: LanguageKey)

}