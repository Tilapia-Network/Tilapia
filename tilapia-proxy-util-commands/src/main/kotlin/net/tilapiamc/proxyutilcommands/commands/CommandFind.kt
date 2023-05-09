package net.tilapiamc.proxyutilcommands.commands

import com.velocitypowered.api.proxy.Player
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.tilapiamc.proxyapi.command.*
import net.tilapiamc.proxyapi.command.args.playerArg
import net.tilapiamc.proxyapi.game.Lobby
import net.tilapiamc.proxyapi.game.MiniGame
import net.tilapiamc.proxyapi.player.PlayersManager.Companion.getLocalPlayer

class CommandFind: ProxyCommand("find", "查看玩家所在的小遊戲", false) {

    val unknown = getCommandLanguageKey("UNKNOWN", "&c玩家在一個未知的遊戲")
    val miniGame = getCommandLanguageKey("MINIGAME", "&a玩家正在小遊戲 %1\$s  (種類 %2\$s)")
    val lobby = getCommandLanguageKey("LOBBY", "&a玩家正在大廳 %1\$s  (種類 %2\$s)")
    val clickToTeleport = getCommandLanguageKey("CLICK_TO_TELEPORT", "&e[點我傳送]")
    val clickToForceJoin = getCommandLanguageKey("CLICK_TO_FORCE_JOIN", "&e[點我強制加入]")
    val clickToSpectate = getCommandLanguageKey("CLICK_TO_SPECTATE", "&e[點我觀戰]")
    val execute = getCommandLanguageKey("EXECUTE", "&e點我傳送至該玩家所在的遊戲")
    val forceJoin = getCommandLanguageKey("FORCE_JOIN", "&e點我強制加入該玩家所在的遊戲")
    val spectate = getCommandLanguageKey("SPECTATE", "&e點我觀戰玩家所在的遊戲")

    init {
        val player by playerArg("Player")

        onCommand {
            val localPlayer = player()!!.getLocalPlayer()
            val game = localPlayer.where()
            if (game is Lobby) {
                sender.sendMessage("")
                sender.sendMessage(getLanguageBundle()[lobby].format(game.shortGameId, game.lobbyType))
                sender.sendMessage(getLanguageBundle()[clickToTeleport].toComponent()
                    .clickEvent(ClickEvent.runCommand("/send ${game.shortGameId}"))
                    .hoverEvent(HoverEvent.showText(getLanguageBundle()[execute].toComponent()))
                    .append("    ".toComponent())
                    .append((getLanguageBundle()[clickToForceJoin]).toComponent()
                        .clickEvent(ClickEvent.runCommand("/send ${game.shortGameId} ${(sender as Player).username} force-join"))
                        .hoverEvent(HoverEvent.showText(getLanguageBundle()[forceJoin].toComponent()))
                    )
                )
                sender.sendMessage("")
            } else if (game is MiniGame) {
                sender.sendMessage("")
                sender.sendMessage(getLanguageBundle()[miniGame].format(game.shortGameId, game.miniGameType))
                sender.sendMessage(getLanguageBundle()[clickToTeleport].toComponent()
                    .clickEvent(ClickEvent.runCommand("/send ${game.shortGameId}"))
                    .hoverEvent(HoverEvent.showText(getLanguageBundle()[execute].toComponent()))
                    .append("    ".toComponent())
                    .append((getLanguageBundle()[clickToSpectate]).toComponent()
                        .clickEvent(ClickEvent.runCommand("/spectate ${game.shortGameId}"))
                        .hoverEvent(HoverEvent.showText(getLanguageBundle()[spectate].toComponent()))
                    )
                    .append("    ".toComponent())
                    .append((getLanguageBundle()[clickToForceJoin]).toComponent()
                        .clickEvent(ClickEvent.runCommand("/send ${game.shortGameId} ${(sender as Player).username} force-join"))
                        .hoverEvent(HoverEvent.showText(getLanguageBundle()[forceJoin].toComponent()))
                    )
                )
                sender.sendMessage("")
            } else {
                sender.sendMessage(getLanguageBundle()[unknown])
            }
            true
        }
    }

}