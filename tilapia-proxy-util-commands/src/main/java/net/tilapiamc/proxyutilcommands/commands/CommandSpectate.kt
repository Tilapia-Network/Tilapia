package net.tilapiamc.proxyutilcommands.commands

import com.velocitypowered.api.proxy.Player
import net.tilapiamc.proxyapi.command.ProxyCommand
import net.tilapiamc.proxyapi.command.args.gameIdArg
import net.tilapiamc.proxyapi.command.args.playerArg
import net.tilapiamc.proxyapi.command.getCommandLanguageKey
import net.tilapiamc.proxyapi.command.getLanguageBundle
import net.tilapiamc.proxyapi.command.sendMessage
import net.tilapiamc.proxyapi.game.MiniGame
import net.tilapiamc.proxyapi.player.PlayersManager.Companion.getLocalPlayer

class CommandSpectate: ProxyCommand("spectate", "強制玩家觀戰一局遊戲", false) {

    val success = getCommandLanguageKey("SUCCESS", "&a成功讓 &a%1\$s 觀戰 &e%2\$s")
    val alreadyIn = getCommandLanguageKey("ALREADY_IN", "&c該玩家早就在觀戰此遊戲")
    val spectateLobbyNotSupported = getCommandLanguageKey("SPECTATE_LOBBY_NOT_SUPPORT", "&c你無法旁觀一個大廳")

    init {
        val game by gameIdArg("Game", { true })
        val player by playerArg("Player", isRequired = false)

        onCommand {
            val localPlayer = player()?.getLocalPlayer()?:(sender as Player).getLocalPlayer()
            val targetGame = game()!!
            val original = localPlayer.where()
            if (original?.gameId == targetGame.gameId) {
                sender.sendMessage(getLanguageBundle()[alreadyIn])
                return@onCommand true
            }
            if (targetGame !is MiniGame) {
                sender.sendMessage(getLanguageBundle()[spectateLobbyNotSupported])
                return@onCommand true
            }
            localPlayer.send(targetGame, false, true)
            sender.sendMessage("")
            sender.sendMessage(getLanguageBundle()[success].format(localPlayer.playerName, targetGame.shortGameId))
            sender.sendMessage("")
            true
        }
    }

}