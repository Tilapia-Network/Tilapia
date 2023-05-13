package net.tilapiamc.customib.block

import me.fan87.plugindevkit.PluginInstanceGrabber
import net.minecraft.server.v1_8_R3.NBTTagCompound
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.common.events.annotation.Subscribe
import net.tilapiamc.customib.item.CustomItem
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.ItemStack

class ItemBlock(val blocksManager: BlocksManager, val block: CustomBlock): CustomItem(block.key, block.displayName) {

    override val material: Material = block.material
    override val damage: Short = block.damage

    override fun applyDisplays(player: LocalNetworkPlayer, itemStack: ItemStack): ItemStack {
        return itemStack
    }

    override fun applyExtraNBT(nbt: NBTTagCompound) {

    }

    @Subscribe("itemBlock-onBlockPlaced")
    fun onBlockPlaced(event: BlockPlaceEvent) {
        if (isAnItemOf(event.itemInHand)) {
            blocksManager.setBlock(event.block, block, false)
        }
    }

}