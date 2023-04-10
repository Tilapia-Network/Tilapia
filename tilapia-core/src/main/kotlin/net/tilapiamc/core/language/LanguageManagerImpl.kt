package net.tilapiamc.core.language

import net.tilapiamc.api.language.LanguageBundle
import net.tilapiamc.api.language.LanguageManager
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class LanguageManagerImpl: LanguageManager {

    val bundleCache = HashMap<Locale, LanguageBundle>()

    private fun createLanguageBundle(locale: Locale): LanguageBundle {
        return LanguageBundleImpl(hashMapOf())
    }

    override fun getLanguageBundle(locale: Locale): LanguageBundle {
        return bundleCache[locale]?:createLanguageBundle(locale).also { bundleCache[locale] = it }
    }
}