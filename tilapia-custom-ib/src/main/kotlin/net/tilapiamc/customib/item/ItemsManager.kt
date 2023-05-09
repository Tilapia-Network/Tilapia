package net.tilapiamc.customib.item

import net.tilapiamc.customib.NamespacedKey
import net.tilapiamc.spigotcommon.game.event.GameEventManager

class ItemsManager(val eventManager: GameEventManager) {

    init {
        eventManager.registerListener(this)
    }

    private val _customItems = HashMap<NamespacedKey, CustomItem>()
    val customItems: Map<NamespacedKey, CustomItem>
        get() = _customItems

    fun registerItem(item: CustomItem) {
        _customItems[item.key] = item
        eventManager.registerListener(item)
    }

    // Item display name & lore translation

}