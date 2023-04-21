package net.tilapiamc.autoop

import com.mojang.authlib.Agent
import com.mojang.authlib.GameProfile
import com.mojang.authlib.ProfileLookupCallback
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.TilapiaPlugin
import net.tilapiamc.api.commands.*
import net.tilapiamc.api.commands.args.PlayerNotFoundException
import net.tilapiamc.api.events.EventsManager
import net.tilapiamc.api.events.game.PlayerJoinGameEvent
import net.tilapiamc.autoop.tables.TableOpList
import net.tilapiamc.command.CommandExecution
import net.tilapiamc.command.args.CommandArgument
import net.tilapiamc.common.events.EventListener
import net.tilapiamc.database.blockingDbQuery
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.net.Proxy
import java.util.*
import kotlin.reflect.KProperty

class AutoOpPlugin: TilapiaPlugin() {

    companion object {
        val authenticationService = YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString())
        val sessionService = authenticationService.createMinecraftSessionService()
        val profileRepository = authenticationService.createProfileRepository()
        fun getUUIDFromPlayerName(name: String): UUID? {
            if (Bukkit.getPlayer(name)?.uniqueId != null) {
                return Bukkit.getPlayer(name)?.uniqueId
            }
            val lock = Object()
            var answered = false
            var response: UUID? = null
            profileRepository.findProfilesByNames(arrayOf(name), Agent.MINECRAFT, object : ProfileLookupCallback {
                override fun onProfileLookupSucceeded(p0: GameProfile) {
                    answered = true
                    response = p0.id
                    synchronized(lock) {
                        lock.notifyAll()
                    }
                }

                override fun onProfileLookupFailed(p0: GameProfile?, p1: Exception?) {
                    p1?.printStackTrace()
                    answered = true
                    response = p0?.id
                    synchronized(lock) {
                        lock.notifyAll()
                    }
                }
            })
            if (!answered) {
                synchronized(lock) {
                    lock.wait(10000)
                }
            }
            return response
        }
    }

    init {
        requireSchemaAccess("AutoOp")
    }

    lateinit var database: Database
    override fun onEnable() {
        database = TilapiaCore.instance.getDatabase("AutoOp")

        blockingDbQuery(database) {
            SchemaUtils.createMissingTablesAndColumns(TableOpList)
        }
        EventsManager.registerListener(EventListener("autoOp", setOf(), setOf() ) {
            if (it is PlayerJoinGameEvent) {
                Thread {
                    blockingDbQuery(database) {
                        it.player.isOp = TableOpList.select { TableOpList.playerUuid.eq(it.player.uniqueId) }.any()
                    }
                }.start()

            }
        })
        SpigotCommandsManager.registerCommand(object : BukkitCommand("op", "給予一個玩家管理員權限", true) {
            val success = getCommandLanguageKey("SUCCESS", "${ChatColor.GREEN}成功給予 %1\$s 管理員權限。玩家可能要重新加入才能生效")
            init {
                val playerName by addArgument(OpTargetArgument("PlayerName", false))

                onCommand {
                    val uuid = playerName()
                    if (uuid == null) {
                        Thread {
                            sender.sendMessage("")
                            blockingDbQuery(database) {
                                for (resultRow in TableOpList.selectAll()) {
                                    val playerName = resultRow[TableOpList.playerNameCache]
                                    val uuid = resultRow[TableOpList.playerUuid]
                                    val component = TextComponent(" ${ChatColor.GRAY}- ${ChatColor.GREEN}$playerName (${uuid.toString().split("-")[0]})  ").apply {
                                        addExtra(TextComponent("${ChatColor.RED}[DEOP]").apply {
                                            hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(TextComponent("${ChatColor.GRAY}/deop $uuid")))
                                            clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deop $uuid")
                                        })
                                    }
                                    requiresPlayer().spigot().sendMessage(component)
                                }
                            }
                            sender.sendMessage("")
                        }.start()
                        return@onCommand true
                    }
                    blockingDbQuery(database) {
                        if (!TableOpList.select { TableOpList.playerUuid.eq(uuid) }.any()) {
                            TableOpList.insert {
                                it[this.playerNameCache] = parsedArgs.first()
                                it[this.playerUuid] = uuid
                            }
                        }
                    }
                    sender.sendMessage(getLanguageBundle()[success].format(parsedArgs[0]))
                    true
                }
            }
        })
        SpigotCommandsManager.registerCommand(object : BukkitCommand("deop", "移除一個玩家的管理員權限", true) {
            val success = getCommandLanguageKey("SUCCESS", "${ChatColor.GREEN}成功移除 %1\$s 的管理員權限")
            val playerNotFound = getCommandLanguageKey("PLAYER_NOT_FOUND", "${ChatColor.RED}找不到該玩家")
            init {
                val playerName by addArgument(OpTargetArgument("PlayerName", true))

                onCommand {
                    val uuid = playerName()?:commandError(getLanguageBundle()[playerNotFound])
                    blockingDbQuery(database) {
                        TableOpList.deleteWhere { this.playerUuid.eq(uuid) }
                    }
                    sender.sendMessage(getLanguageBundle()[success].format(parsedArgs[0]))
                    true
                }
            }
        })
    }



}

class OpTargetArgument(name: String, isRequired: Boolean): CommandArgument<UUID, CommandSender>(name, isRequired) {
    override fun getValue(any: Any?, property: KProperty<*>): CommandExecution<CommandSender>.() -> UUID? {

        return {

            getArgString()?.let {
                try {
                    UUID.fromString(it)
                } catch (e: IllegalArgumentException) {
                    AutoOpPlugin.getUUIDFromPlayerName(it)?:throw PlayerNotFoundException(it)
                }
            }
        }
    }

    override fun tabComplete(sender: CommandSender, token: String): Collection<String> {
        return Bukkit.getOfflinePlayers().filter { it.name.lowercase().startsWith(token.lowercase()) }.map { it.name }
    }
}

