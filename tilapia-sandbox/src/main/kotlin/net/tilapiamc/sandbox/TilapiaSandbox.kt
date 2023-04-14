package net.tilapiamc.sandbox

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.getSenderLanguageBundle
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.game.getGameLanguageKey
import net.tilapiamc.api.language.LanguageKeyDelegation
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.NetworkPlayer
import net.tilapiamc.api.utils.HashMapPlayerProvider
import net.tilapiamc.api.utils.PlayerBasedProvider
import net.tilapiamc.gameextension.plugins.PluginBossBar1_8_8
import net.tilapiamc.spigotcommon.game.lobby.LocalLobby
import org.bukkit.ChatColor
import org.bukkit.World
import org.bukkit.entity.Player


class TilapiaSandbox(core: TilapiaCore, world: World): LocalLobby(core, world, "sandbox") {

    companion object {
        val SANDBOX_BOSS_BAR by LanguageKeyDelegation("您正在沙盒地圖")
        init {
            TilapiaCore.instance.languageManager.registerLanguageKey(SANDBOX_BOSS_BAR)
        }
    }

    val rainbowTextProvider = BossBarTextProvider()

    init {
        applyPlugin(PluginBossBar1_8_8 { rainbowTextProvider(it) + "  ${ChatColor.RESET}${ChatColor.YELLOW}${world.name}" })
    }

    override fun onStart() {

    }

    override fun onEnd() {

    }




    val forceJoinOnly = getGameLanguageKey("FORCE_JOIN_ONLY", "沙盒世界必須使用強制加入")
    override fun couldAddPlayer(networkPlayer: NetworkPlayer, forceJoin: Boolean): ManagedGame.PlayerJoinResult {
        return if (forceJoin)
            ManagedGame.PlayerJoinResult(ManagedGame.PlayerJoinResultType.ACCEPTED, 1.0)
        else
            ManagedGame.PlayerJoinResult(ManagedGame.PlayerJoinResultType.DENIED, 0.0, networkPlayer.getLanguageBundle()[forceJoinOnly])
    }

    val joinMessage = getGameLanguageKey("JOIN_MESSAGE", "${ChatColor.GREEN}[+] %1\$s")
    val quitMessage = getGameLanguageKey("QUIT_MESSAGE", "${ChatColor.RED}[-] %1\$s")
    override fun addPlayer(networkPlayer: LocalNetworkPlayer) {
        localPlayers.forEach {
            it.sendMessage(it.getLanguageBundle()[joinMessage].format(networkPlayer.nameWithPrefix))
        }
    }

    override fun removePlayer(networkPlayer: LocalNetworkPlayer) {
        localPlayers.forEach {
            it.sendMessage(it.getLanguageBundle()[quitMessage].format(networkPlayer.nameWithPrefix))
        }
    }
    inner class BossBarTextProvider: PlayerBasedProvider<String>() {

        val frame = HashMapPlayerProvider(0)

        fun Player.getBossBarText(): String {
            return getSenderLanguageBundle()[SANDBOX_BOSS_BAR]
        }

        // 0 ~ length * 3  - Color iteration
        // length * 3 ~ +120  ~  Stable Color
        // length * 3 ~ +30  ~  Color Blinking
        fun getTotalFrame(player: Player): Int {
            return player.getBossBarText().length * 3 + 120 + 30
        }

        override fun invoke(player: Player): String {
            frame[player] = (frame[player] + 1) % getTotalFrame(player)
            val currentFrameNumber = frame[player]
            val text = player.getBossBarText()
            val stageOne = player.getBossBarText().length * 3
            val stageTwo = stageOne + 120
            val stageThree = stageTwo + 30
            if (currentFrameNumber in 0 until stageOne) {
                val index = ((currentFrameNumber + 1) / 3)
                return "${ChatColor.GOLD}${ChatColor.BOLD}${text.substring(0, index)}${ChatColor.YELLOW}${ChatColor.BOLD}${text.substring(index)}"
            } else if (currentFrameNumber in stageOne until stageTwo) {
                return "${ChatColor.GOLD}${ChatColor.BOLD}${text}"
            } else if (currentFrameNumber in stageTwo until stageThree) {
                val index = currentFrameNumber - stageTwo
                if ((index / 10) % 2 == 0) {
                    return "${ChatColor.YELLOW}${ChatColor.BOLD}${text}"
                } else {
                    return "${ChatColor.GOLD}${ChatColor.BOLD}${text}"
                }
            }
            return text
        }

        override fun onJoin(player: Player) {

        }

        override fun onQuit(player: Player) {

        }

    }

}

