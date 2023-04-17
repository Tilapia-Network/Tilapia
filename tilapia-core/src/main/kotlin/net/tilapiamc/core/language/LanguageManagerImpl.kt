package net.tilapiamc.core.language

import net.tilapiamc.common.language.LanguageBundle
import net.tilapiamc.common.language.LanguageKey
import net.tilapiamc.common.language.LanguageManager
import org.apache.logging.log4j.LogManager
import java.util.*

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
        logger.debug("Registered language key: ${languageKey.name}")
        registered.add(languageKey)
    }
}