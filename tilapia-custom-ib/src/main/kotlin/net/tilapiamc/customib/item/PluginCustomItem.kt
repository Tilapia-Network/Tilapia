package net.tilapiamc.customib.item

import net.tilapiamc.spigotcommon.game.plugin.GamePlugin

class PluginCustomItem(vararg val items: CustomItem): GamePlugin() {
    val itemsManager by lazy { ItemsManager(eventManager) }
    override fun onEnable() {
        for (item in items) {
            itemsManager.registerItem(item)
        }
    }

    override fun onDisable() {

    }

}