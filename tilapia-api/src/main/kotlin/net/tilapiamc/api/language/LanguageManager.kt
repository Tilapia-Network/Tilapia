package net.tilapiamc.api.language

import java.util.*

interface LanguageManager {

    fun getLanguageBundle(locale: Locale): LanguageBundle
    fun registerLanguageKey(languageKey: LanguageKey)

}