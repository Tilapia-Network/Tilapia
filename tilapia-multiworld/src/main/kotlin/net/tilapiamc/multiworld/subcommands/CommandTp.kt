package net.tilapiamc.multiworld.subcommands

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.BukkitSubCommand
import net.tilapiamc.api.commands.getLanguageBundle
import net.tilapiamc.api.commands.getCommandLanguageKey
import net.tilapiamc.api.commands.requiresPlayer
import net.tilapiamc.command.args.impl.stringArg
import net.tilapiamc.multiworld.WorldManager
import net.tilapiamc.multiworld.args.worldNameArg
import net.tilapiamc.spigotcommon.game.LocalGame
import net.tilapiamc.spigotcommon.game.minigame.LocalMiniGame
import org.bukkit.Bukkit
import org.bukkit.ChatColor

fun commandTp(): BukkitSubCommand.() -> Unit {

    return {
        val warningWorldNotRegistered = getCommandLanguageKey("WARNING_WORLD_NOT_REGISTERED", "${ChatColor.RED}該世界並未被註冊，請使用 /mw import 註冊此世界！")
        val warningWorldIsManaged = getCommandLanguageKey("WARNING_WORLD_IS_MANAGED", "${ChatColor.RED}該世界正在運行一局遊戲(大廳或小遊戲)，加入可能會導致未預期的錯誤。若要繼續，請使用 \"/mv tp <世界名稱> confirm\"")
        val errorAlreadyInTheWorld = getCommandLanguageKey("ERROR_ALREADY_IN_THE_WORLD", "${ChatColor.RED}你早就在這個世界裡了")
        val success = getCommandLanguageKey("SUCCESS", "${ChatColor.GREEN}成功傳送至 ${ChatColor.YELLOW}%1\$s")
        addAlias("teleport")
        val worldName by worldNameArg("World", requireRegistered = false, requireLoaded = true, checkIllegal = false)
        val confirm by stringArg("Confirm", isRequired = false)
        onCommand {
            val player = requiresPlayer()
            val worldName = worldName()!!
            val registered = worldName.lowercase() in WorldManager.registeredWorlds.map { it.name.lowercase() }
            val world = Bukkit.getWorlds().first { it.name == worldName }
            if (player.world == world) {
                sender.sendMessage("")
                sender.sendMessage(getLanguageBundle()[errorAlreadyInTheWorld])
                sender.sendMessage("")
                return@onCommand true
            }
            if (confirm() == null) {
                for (allGame in TilapiaCore.instance.gamesManager.getAllLocalGames()) {
                    if (allGame is LocalGame) {
                        if (world == allGame.gameWorld) {
                            sender.sendMessage("")
                            sender.sendMessage(getLanguageBundle()[warningWorldIsManaged])
                            sender.sendMessage("")
                            return@onCommand true
                        }
                    }
                }
            }

            sender.sendMessage("")
            if (WorldManager.checkName(worldName) && !registered) {
                sender.sendMessage(getLanguageBundle()[warningWorldNotRegistered])
                sender.sendMessage("")
            }
            player.teleport(world.spawnLocation)
            sender.sendMessage(getLanguageBundle()[success].format(world.name))
            true
        }
    }
}