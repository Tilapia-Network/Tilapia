package net.tilapiamc.core.language

import net.tilapiamc.api.language.LanguageBundle
import net.tilapiamc.api.language.LanguageKey
import net.tilapiamc.api.language.LanguageManager
import org.apache.logging.log4j.LogManager
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object LanguageManagerImpl: LanguageManager {
    val logger = LogManager.getLogger("LanguageManager")

    val bundleCache = HashMap<Locale, LanguageBundle>()
    val registered = ArrayList<LanguageKey>()

    private fun createLanguageBundle(locale: Locale): LanguageBundle {
        return LanguageBundleImpl(hashMapOf())
    }

    override fun getLanguageBundle(locale: Locale): LanguageBundle {
        return bundleCache[locale]?:createLanguageBundle(locale).also { bundleCache[locale] = it }
    }

    fun verifyLanguageKeyRegistration(languageKey: LanguageKey) {
        if (languageKey !in registered) {
            registerLanguageKey(languageKey)
            logger.warn("Language key ${languageKey.name} is not registered!")
        }
    }

    override fun registerLanguageKey(languageKey: LanguageKey) {
        logger.info("Registered language key: ${languageKey.name}")
        registered.add(languageKey)
    }
}