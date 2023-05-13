package net.tilapiamc.customib

import net.tilapiamc.customib.block.BlocksManager
import net.tilapiamc.customib.events.BlockUpdateEvent
import net.tilapiamc.customib.item.CustomItem
import net.tilapiamc.customib.item.ItemsManager
import net.tilapiamc.spigotcommon.game.plugin.GamePlugin

class PluginCustomItemBlocks(val registerContent: PluginCustomItemBlocks.() -> Unit): GamePlugin() {
    val itemsManager by lazy { ItemsManager(eventManager) }
    val blocksManager by lazy { BlocksManager(game.gameWorld, eventManager, itemsManager) }

    init {
        BlockUpdateEvent.init()
    }

    override fun onEnable() {
        registerContent()
    }

    override fun onDisable() {

    }

}