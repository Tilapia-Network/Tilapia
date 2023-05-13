package net.tilapiamc.fleetwars.customitems

import me.fan87.plugindevkit.events.EntityTickEvent
import me.fan87.plugindevkit.events.ServerTickEvent
import net.minecraft.server.v1_8_R3.NBTTagCompound
import net.tilapiamc.common.events.annotation.Subscribe
import net.tilapiamc.customib.NamespacedKey
import net.tilapiamc.customib.block.BlockEntityProvider
import net.tilapiamc.customib.block.BlocksManager
import net.tilapiamc.customib.block.CustomBlock
import net.tilapiamc.customib.block.CustomBlockEntity
import org.bukkit.Effect
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class BlockSpeedPad(val blocksManager: BlocksManager): CustomBlock(NamespacedKey("fleetwars", "speedpad"), "加速盤"), BlockEntityProvider {

    override val material: Material = Material.GOLD_BLOCK
    override val damage: Short = 0

    override fun setBlockType(block: Block) {
        block.type = Material.GOLD_BLOCK
    }

    @Subscribe("fleetwars-speedpad-onEntityTick")
    fun onEntityTick(event: EntityTickEvent) {
        if (event.entity is Player) {
            if (blocksManager.getBlock(event.entity.location.subtract(0.0, 0.1, 0.0)) == this) {
                (event.entity as Player).addPotionEffect(PotionEffect(PotionEffectType.SPEED, 1, 3, true, false))
            }
        }
    }

    override fun createBlockEntity(block: Block): CustomBlockEntity {
        return BlockEntitySpeedPad(block)
    }

}

class BlockEntitySpeedPad(block: Block): CustomBlockEntity(block, NamespacedKey("fleetwars", "speedpad")) {
    @Subscribe("speedPad-onTick")
    fun onTick(event: ServerTickEvent) {
        block.world.playEffect(block.location.add(0.5, 0.5, 0.5), Effect.CRIT, 0, 4)
    }
}