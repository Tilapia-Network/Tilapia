package net.tilapiamc.multiworld.subcommands

import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.commands.BukkitSubCommand
import net.tilapiamc.api.commands.getCommandLanguageKey
import net.tilapiamc.api.commands.getLanguageBundle
import net.tilapiamc.command.args.impl.stringEnumArg
import net.tilapiamc.multiworld.args.worldNameArg
import net.tilapiamc.spigotcommon.game.LocalGame
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandException

fun commandUnload(): BukkitSubCommand.() -> Unit {

    return {
        val warningWorldIsManaged = getCommandLanguageKey("WARNING_WORLD_IS_MANAGED", "${ChatColor.RED}此世界正在被一個運行中的遊戲載入(遊戲ID: %1\$s)，強行卸載將會導致嚴重錯誤")
        val success = getCommandLanguageKey("SUCCESS", "${ChatColor.GREEN}成功卸載 ${ChatColor.YELLOW}%1\$s ${ChatColor.GREEN}！")
        val failed = getCommandLanguageKey("FAILED", "${ChatColor.RED}因為未知的原因無法卸載 %1\$s ${ChatColor.RED}！")
        val worldName by worldNameArg("WorldName", false, true, true)
        val dontSave by stringEnumArg("DontSave", { arrayListOf("dont-save")}, isRequired = false)
        onCommand {
            val worldName = worldName()
            val world = Bukkit.getWorld(worldName)
            for (allGame in TilapiaCore.instance.localGameManager.getAllLocalGames()) {
                if (allGame is LocalGame) {
                    if (world == allGame.gameWorld) {
                        sender.sendMessage("")
                        sender.sendMessage(getLanguageBundle()[warningWorldIsManaged])
                        sender.sendMessage("")
                        return@onCommand true
                    }
                }
            }
            try {
                if (Bukkit.unloadWorld(world, dontSave() == null)) {
                    sender.sendMessage(getLanguageBundle()[failed].format(worldName))
                    return@onCommand false
                }
            } catch (e: IllegalArgumentException) {
                throw CommandException(e.message)
            }
            sender.sendMessage(getLanguageBundle()[success].format(worldName))
            true
        }
    }
}