package net.tilapiamc.utilcommands.commands

import me.fan87.plugindevkit.PluginInstanceGrabber
import net.minecraft.server.v1_8_R3.Block
import net.minecraft.server.v1_8_R3.BlockPosition
import net.minecraft.server.v1_8_R3.BlockState
import net.minecraft.server.v1_8_R3.BlockStateInteger
import net.minecraft.server.v1_8_R3.BlockStateList
import net.minecraft.server.v1_8_R3.BlockTorch
import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.BukkitCommand
import net.tilapiamc.api.commands.args.playerArg
import net.tilapiamc.api.commands.requiresPlayer
import net.tilapiamc.api.hook.MethodTransformer
import net.tilapiamc.api.hook.TransformManager
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.api.utils.ASMUtils
import net.tilapiamc.command.args.impl.stringEnumArg
import net.tilapiamc.common.language.LanguageKeyDelegation
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodNode

class CommandTest: BukkitCommand("test", "測試用指令", true) {


    init {

        val mode by stringEnumArg("Mode", { arrayListOf("get", "set") }, isRequired = true)

        onCommand {
            val tilapiaBlockID = TilapiaCustomBlockCallback.getProperty()
            val player = requiresPlayer()
            val targetBlock = player.location.add(0.0, -1.0, 0.0).block
            val world = (player.world as CraftWorld).handle
            val blockPosition = BlockPosition(targetBlock.location.x, targetBlock.location.y, targetBlock.location.z)
            if (mode() == "set") {
                targetBlock.type = Material.SPONGE
                val blockData = world.getType(blockPosition)
                blockData.set(tilapiaBlockID, 69)
                world.setTypeUpdate(blockPosition, blockData)
            }
            if (mode() == "get") {
                val blockData = world.getType(blockPosition)
                if (tilapiaBlockID in blockData.a()) {
                    sender.sendMessage("Data: ${blockData.get(tilapiaBlockID)}")
                } else {
                    sender.sendMessage("Nah you got trolled")
                }
            }
            true
        }

        canUseCommand {
            isOp && this is Player
        }
    }

}

object TilapiaCustomBlockCallback {
    @JvmStatic
    private val tilapiaBlockID = BlockStateInteger.of("TilapiaBlockID", 0, Short.MAX_VALUE.toInt())

    fun getProperty(): BlockState<Int> {
        return tilapiaBlockID
    }

    @JvmStatic
    fun getNewBlockStateList(old: BlockStateList): BlockStateList {
        return BlockStateList(old.block, *old.d().toTypedArray(), getProperty())
    }
}