package net.tilapiamc.clientintegration.commands

import net.minecraft.server.v1_8_R3.EntityPlayer
import net.minecraft.server.v1_8_R3.PacketPlayInFlying
import net.minecraft.server.v1_8_R3.PlayerConnection
import net.tilapiamc.api.commands.BukkitCommand
import net.tilapiamc.api.commands.getCommandLanguageKey
import net.tilapiamc.api.commands.requiresPlayer
import net.tilapiamc.api.hook.MethodTransformer
import net.tilapiamc.api.hook.TransformManager
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.api.utils.ASMUtils
import net.tilapiamc.api.utils.LocalVarManager
import net.tilapiamc.command.args.impl.stringEnumArg
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*
import java.util.*
import kotlin.reflect.jvm.javaMethod

val enabledPlayers = ArrayList<UUID>()

class CommandNoClip: BukkitCommand("noclip", "啟用或停用穿牆模式", true) {


    init {
        TransformManager.transformClass(object : MethodTransformer(PlayerConnection::class.java.getDeclaredMethod("a", PacketPlayInFlying::class.java)) {
            override fun transform(methodNode: MethodNode) {
                val varManager = LocalVarManager(methodNode)
                val out = InsnList()
                for (instruction in methodNode.instructions) {
                    out.add(instruction)
                    if (instruction is FieldInsnNode) {
                        if (instruction.name == "noclip") {
                            out.add(InsnNode(Opcodes.POP))
                            out.add(VarInsnNode(Opcodes.ALOAD, 0))
                            out.add(ASMUtils.generateGetField(PlayerConnection::player))
                            out.add(ASMUtils.reflectionMethodInvoke(NoClipHook::isNoClipping.javaMethod!!, varManager))
                        }
                    }
                }
                methodNode.instructions = out
            }

        })
    }

    val enabledMsg = getCommandLanguageKey("NOCLIP_ENABLED", "${ChatColor.GREEN}成功啟用穿牆模式")
    val disabledMsg = getCommandLanguageKey("NOCLIP_DISABLED", "${ChatColor.RED}成功停用穿牆模式")

    init {
        val mode by stringEnumArg("Mode", { arrayListOf("enable", "disable") }, isRequired = false)

        onCommand {
            val targetPlayer = requiresPlayer().getLocalPlayer()
            val enabled = if (mode() == null) targetPlayer.uniqueId !in enabledPlayers else mode() == "enable"
            if (enabled) {
                targetPlayer.sendMessage("TILAPIA_INTERNAL_CLIENT_NOTIFY_NOCLIP_TRUE")
                targetPlayer.sendMessage(targetPlayer.getLanguageBundle()[enabledMsg])
                enabledPlayers.add(targetPlayer.uniqueId)
            } else {
                targetPlayer.sendMessage("TILAPIA_INTERNAL_CLIENT_NOTIFY_NOCLIP_FALSE")
                targetPlayer.sendMessage(targetPlayer.getLanguageBundle()[disabledMsg])
                enabledPlayers.remove(targetPlayer.uniqueId)

            }
            true
        }

        canUseCommand {
            isOp && this is Player
        }
    }

}

object NoClipHook {
    @JvmStatic
    fun isNoClipping(player: EntityPlayer): Boolean {
        return player.uniqueID in enabledPlayers || player.noclip
    }
}