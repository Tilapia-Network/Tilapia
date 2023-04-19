package net.tilapiamc.proxyutilcommands.commands

import com.velocitypowered.api.proxy.Player
import net.tilapiamc.command.args.impl.stringEnumArg
import net.tilapiamc.proxyapi.command.ProxyCommand
import net.tilapiamc.proxyapi.command.args.gameIdArg
import net.tilapiamc.proxyapi.command.args.playerArg
import net.tilapiamc.proxyapi.command.getCommandLanguageKey
import net.tilapiamc.proxyapi.command.getLanguageBundle
import net.tilapiamc.proxyapi.command.sendMessage
import net.tilapiamc.proxyapi.game.MiniGame
import net.tilapiamc.proxyapi.player.PlayersManager.Companion.getLocalPlayer

class CommandSend: ProxyCommand("send", "傳送一個玩家到一局遊戲", false) {

    val success = getCommandLanguageKey("SUCCESS", "&a成功傳送 &e%1\$s &a到 &e%2\$s")
    val failed = getCommandLanguageKey("FAILED", "&c無法傳送 &e%1\$s &c到 &e%2\$s &c！原因: %3\$s")
    val alreadyIn = getCommandLanguageKey("ALREADY_IN", "&c該玩家早就在此遊戲")
    val spectateLobbyNotSupported = getCommandLanguageKey("SPECTATE_LOBBY_NOT_SUPPORT", "&c你無法旁觀一個大廳")

    init {
        val game by gameIdArg("Game", { true })
        val player by playerArg("Player", isRequired = false)
        val joinMode by stringEnumArg("JoinMode", { listOf("force-join", "spectate", "normal") }, isRequired = false)

        onCommand {
            val localPlayer = player()?.getLocalPlayer()?:(sender as Player).getLocalPlayer()
            val targetGame = game()!!
            val original = localPlayer.where()
            val joinModeText = joinMode()?:"normal"
            if (original?.gameId == targetGame.gameId) {
                sender.sendMessage(getLanguageBundle()[alreadyIn])
                return@onCommand true
            }
            if (joinModeText == "spectate" && targetGame !is MiniGame) {
                sender.sendMessage(getLanguageBundle()[spectateLobbyNotSupported])
                return@onCommand true
            }
            val result = localPlayer.send(targetGame, joinModeText == "force-join", joinModeText == "spectate")
            if (result.success) {
                sender.sendMessage("")
                sender.sendMessage(getLanguageBundle()[success].format(localPlayer.playerName, targetGame.shortGameId))
                sender.sendMessage("")
            } else {
                sender.sendMessage("")
                sender.sendMessage(getLanguageBundle()[failed].format(localPlayer.playerName, targetGame.shortGameId, result.message))
                sender.sendMessage("")
            }
            true
        }
    }

}