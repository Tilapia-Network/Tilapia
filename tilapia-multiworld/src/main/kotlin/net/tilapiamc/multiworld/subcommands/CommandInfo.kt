package net.tilapiamc.multiworld.subcommands

import net.tilapiamc.api.commands.BukkitSubCommand
import net.tilapiamc.api.commands.getCommandLanguageKey
import net.tilapiamc.api.commands.getLanguageBundle
import net.tilapiamc.api.language.LanguageKey
import net.tilapiamc.multiworld.MultiWorldCommand
import net.tilapiamc.multiworld.WorldManager
import net.tilapiamc.multiworld.args.worldNameArg
import org.bukkit.Bukkit
import org.bukkit.ChatColor

fun commandInfo(): BukkitSubCommand.() -> Unit {

    return {

        val header = getCommandLanguageKey("HEADER", "")
        val infoWorldName = getCommandLanguageKey("WORLD_NAME", "${ChatColor.GRAY}世界名稱： ${ChatColor.YELLOW}")
        val infoGenerator = getCommandLanguageKey("GENERATOR", "${ChatColor.GRAY}生成器名稱： ${ChatColor.YELLOW}")
        val infoWorldType = getCommandLanguageKey("WORLD_TYPE", "${ChatColor.GRAY}世界種類： ${ChatColor.YELLOW}")
        val infoState = getCommandLanguageKey("STATE", "${ChatColor.GRAY}世界狀態： ${ChatColor.YELLOW}")
        val infoSpawnLocation = getCommandLanguageKey("SPAWN_LOCATION", "${ChatColor.GRAY}世界重生點： ${ChatColor.YELLOW}")
        val loadingRequired = getCommandLanguageKey("LOADING_REQUIRED", "${ChatColor.RED}此資訊需要世界被載入")
        val footer = getCommandLanguageKey("FOOTER", "")
        val worldName by worldNameArg("World", requireRegistered = true, requireLoaded = false, isRequired = false)
        onCommand {
            val state: LanguageKey
            val worldName = worldName()!!
            if (Bukkit.getWorld(worldName) != null) {
                state = (parent as MultiWorldCommand).loadedRegistered
            } else {
                state = (parent as MultiWorldCommand).registeredNotLoaded
            }
            val world = WorldManager.getWorld(worldName)!!
            val bukkitWorld = Bukkit.getWorld(worldName)
            sender.sendMessage(getLanguageBundle()[header].format(worldName))
            sender.sendMessage(getLanguageBundle()[infoWorldName] + world.name)
            sender.sendMessage(getLanguageBundle()[infoGenerator] + world.generator + ":" + world.generatorParameters)
            sender.sendMessage(getLanguageBundle()[infoWorldType] + world.worldType)
            sender.sendMessage(getLanguageBundle()[infoState] + getLanguageBundle()[state])
            sender.sendMessage(getLanguageBundle()[infoSpawnLocation] + if (bukkitWorld != null) "(${bukkitWorld.spawnLocation.toVector()})" else getLanguageBundle()[loadingRequired] )
            sender.sendMessage(getLanguageBundle()[footer])

            true
        }
    }
}