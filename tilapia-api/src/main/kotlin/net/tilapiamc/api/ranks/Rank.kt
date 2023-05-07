package net.tilapiamc.api.ranks

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.common.language.LanguageKey
import org.bukkit.ChatColor

class Rank(
    val internalName: String,
    val languageKey: LanguageKey,
    val rankColor: ChatColor,
    val previous: Rank?,
) {

    val permissions = ArrayList<String>()

}