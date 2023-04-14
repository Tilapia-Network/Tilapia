package net.tilapiamc.core.commands

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.BukkitCommand
import net.tilapiamc.api.commands.args.localGameIdArg
import net.tilapiamc.api.commands.requiresPlayer
import net.tilapiamc.api.game.Game
import net.tilapiamc.api.game.minigame.ManagedMiniGame
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import org.bukkit.entity.Player

class CommandSpectateLocal: BukkitCommand("spectate-local", "[開發者] 旁觀一局小遊戲", true) {


    init {
        val game by localGameIdArg("Game", { it is ManagedMiniGame })
        canUseCommand {
            this is Player
        }
        onCommand {
            val core = TilapiaCore.instance
            val player = requiresPlayer()
            val localPlayer = player.getLocalPlayer()
            val game = game()!!

            localPlayer.sendToGame(game as Game, forceJoin = false, spectate = true)

            true
        }
    }

}