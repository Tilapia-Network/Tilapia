package net.tilapiamc.sandbox

import me.fan87.plugindevkit.PluginInstanceGrabber
import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.getSenderLanguageBundle
import net.tilapiamc.api.game.ManagedGame
import net.tilapiamc.api.game.getGameLanguageKey
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.NetworkPlayer
import net.tilapiamc.api.utils.HashMapPlayerProvider
import net.tilapiamc.api.utils.PlayerBasedProvider
import net.tilapiamc.common.language.LanguageKeyDelegation
import net.tilapiamc.gameextension.plugins.PluginBossBar1_8_8
import net.tilapiamc.gameextension.plugins.PluginChat
import net.tilapiamc.multiworld.MultiWorld
import net.tilapiamc.multiworld.WorldManager
import net.tilapiamc.spigotcommon.game.lobby.LocalLobby
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin


class TilapiaSandbox(core: TilapiaCore, world: World): LocalLobby(core, world, "sandbox") {

    companion object {
        val SANDBOX_BOSS_BAR by LanguageKeyDelegation("您正在沙盒地圖")
        init {
            TilapiaCore.instance.languageManager.registerLanguageKey(SANDBOX_BOSS_BAR)
        }
    }

    val rainbowTextProvider = BossBarTextProvider()

    init {
        applyPlugin(PluginChat { player, message -> "${player.nameWithPrefix}${ChatColor.WHITE}: $message" })
        applyPlugin(PluginBossBar1_8_8 { rainbowTextProvider(it) + "  ${ChatColor.RESET}${ChatColor.YELLOW}${world.name}" })
    }

    override fun onStart() {
        setProperty(SandboxProperties.SANDBOX_WORLD, gameWorld.name)
    }

    override fun onEnd() {
        if (Bukkit.getServer().pluginManager.getPlugin("tilapia-core").javaClass.name.contains("dummy")) {
            logger.warn("Skipping auto-save since tilapia-dummy-core is enabled")
            return
        }
        if (Bukkit.getServer().pluginManager.isPluginEnabled("tilapia-multiworld")) {
            logger.info("Saving world: ${gameWorld.name}")
            WorldManager.getWorld(gameWorld.name)?.let { tilapiaWorld ->
                JavaPlugin.getPlugin(MultiWorld::class.java).worldSaveManager.save(
                    "${gameWorld.name}__BACKUP__${System.currentTimeMillis()}",
                    tilapiaWorld,
                    true
                )
            }
        }
    }

    var shutdownNotifyCount = 0

    val serverShuttingDown = getGameLanguageKey("SERVER_SHUTTING_DOWN", "${ChatColor.YELLOW}[沙盒模式] ${ChatColor.RED}伺服器即將更新！請除存世界並盡快退出")

    override fun canShutdown(): Boolean {
        if (shutdownNotifyCount % 5 == 0) {
            for (player in localPlayers) {
                player.playSound(player.location, Sound.NOTE_PIANO, 1f, 1f)
                player.sendMessage(player.getLanguageBundle()[serverShuttingDown])
            }
        }
        shutdownNotifyCount++
        return players.isEmpty()
    }

    val responseShuttingDown = getGameLanguageKey("RESPONSE_SHUTTING_DOWN", "伺服器即將關閉，請等待所有玩家退出再重開沙盒模式")
    val forceJoinOnly = getGameLanguageKey("FORCE_JOIN_ONLY", "沙盒世界必須使用強制加入")
    override fun couldAddPlayer(networkPlayer: NetworkPlayer, forceJoin: Boolean): ManagedGame.PlayerJoinResult {
        return if (forceJoin)
            if (core.shuttingDown)
                ManagedGame.PlayerJoinResult(ManagedGame.PlayerJoinResultType.DENIED, 0.0, networkPlayer.getLanguageBundle()[responseShuttingDown])
            else
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

