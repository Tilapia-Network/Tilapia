package net.tilapiamc.fleetwars

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.common.language.LanguageKey
import org.bukkit.ChatColor
import org.bukkit.inventory.ItemStack
import org.bukkit.Material

enum class CoinType(val filter: (itemStack: ItemStack?) -> Boolean, val displayName: LanguageKey) {

    GOLD({ it?.type == Material.GOLD_INGOT }, LanguageKey("FLEETWARS_COIN_GOLD", "${ChatColor.GOLD}金幣"))
    ;

    init {
        TilapiaCore.instance.languageManager.registerLanguageKey(displayName)
    }

}