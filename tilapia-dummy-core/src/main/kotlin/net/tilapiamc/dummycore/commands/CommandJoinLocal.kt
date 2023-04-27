package net.tilapiamc.dummycore.commands

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.BukkitCommand
import net.tilapiamc.api.commands.requiresPlayer
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.command.args.impl.stringArg
import net.tilapiamc.command.args.impl.stringEnumArg
import net.tilapiamc.language.LanguageCore
import org.bukkit.entity.Player

class CommandJoinLocal: BukkitCommand("join-local", "[開發者] 加入一個伺服器的遊戲", true) {

    companion object {
        const val PERMISSION = "commands.join-local"
    }

    init {
        val miniGameType by stringArg("GameType")
        val force by stringEnumArg("Force", { listOf("force") }, isRequired = false)
        canUseCommand {
            hasPermission(PERMISSION) && this is Player
        }
        onCommand {
            val core = TilapiaCore.instance
            val player = requiresPlayer()
            val localPlayer = player.getLocalPlayer()

            val game = localPlayer.findMiniGameToJoin(miniGameType()!!, force() != null)
                ?: commandError(localPlayer.getLanguageBundle()[LanguageCore.COULD_NOT_FIND_GAME].format(miniGameType()))

            localPlayer.sendToGame(game, force() != null)

            true
        }
    }

}