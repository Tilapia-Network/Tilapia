package net.tilapiamc.utilcommands.commands.gamemode

import net.tilapiamc.api.commands.BukkitCommand
import net.tilapiamc.api.commands.args.playerArg
import net.tilapiamc.api.commands.requiresPlayer
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.command.args.impl.StringEnumArgument
import net.tilapiamc.command.args.impl.enumArg
import net.tilapiamc.command.args.impl.stringEnumArg
import net.tilapiamc.language.LanguageCore
import org.bukkit.GameMode
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandGameMode: BukkitCommand("gamemode", "改變一個玩家的遊戲模式") {

    val gamemodeAliases = hashMapOf(
        GameMode.CREATIVE to listOf("creative", "c", "1"),
        GameMode.SURVIVAL to listOf("survival", "s", "0"),
        GameMode.ADVENTURE to listOf("adventure", "a", "2"),
        GameMode.SPECTATOR to listOf("spectator", "spec", "3"),
    )

    init {
        addAlias("gm")

        val gameMode by stringEnumArg("GameMode", { ArrayList<String>().also {
            for (value in gamemodeAliases.values) {
                it.addAll(value)
            }
        } })
        val player by playerArg("Player", false)

        onCommand {
            val targetPlayer = player()?.getLocalPlayer()?:requiresPlayer().getLocalPlayer()
            val gamemodeString = gameMode()
            targetPlayer.gameMode = gamemodeAliases.entries.first { gamemodeString in it.value }.key
            targetPlayer.sendMessage(targetPlayer.getLanguageBundle()[LanguageCore.COMMAND_GAMEMODE_SUCCESS].format(targetPlayer.gameMode.name))
            true
        }

        canUseCommand {
            isOp && this is Player
        }
    }

}