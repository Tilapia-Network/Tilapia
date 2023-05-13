package net.tilapiamc.customib.block

import org.bukkit.block.Block

interface BlockEntityProvider {

    fun createBlockEntity(block: Block): CustomBlockEntity

}