package net.tilapiamc.api.language

import java.util.Locale

interface LanguageManager {

    fun getLanguageBundle(locale: Locale): LanguageBundle

}