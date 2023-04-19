package net.tilapiamc.lobby

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.api.TilapiaPlugin
import net.tilapiamc.database.TableNews
import net.tilapiamc.database.blockingDbQuery
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

const val DATABASE_NAME = "News"
val hyperLinkRegex = Regex("\\[(.*)\\]\\((.*)\\)")

class TilapiaLobbyPlugin: TilapiaPlugin() {

    init {
        requireSchemaAccess(DATABASE_NAME)
    }

    lateinit var content: String
    lateinit var database: Database

    fun contentToComponent(): Component {
        var component = Component.empty()
        val found = hyperLinkRegex.findAll(content)
        var lastIndex = 0
        for (matchResult in found.iterator()) {
            val pre = content.substring(lastIndex, matchResult.range.first)
            if (pre.isNotEmpty()) {
                component = component.append(Component.text(pre))
            }
            component = component.append(
                Component.text(matchResult.groups[1]!!.value)
                .hoverEvent(HoverEvent.showText(Component.text(matchResult.groups[2]!!.value).color(NamedTextColor.GRAY)))
                .clickEvent(ClickEvent.openUrl(matchResult.groups[2]!!.value)))
            lastIndex = matchResult.range.last + 1
        }
        if (lastIndex != content.length) {
            component = component.append(Component.text(content.substring(lastIndex)))
        }
        return component
    }

    override fun onEnable() {
        NewsConfig.reload()

        database = TilapiaCore.instance.getDatabase(DATABASE_NAME)

        blockingDbQuery(database) {
            SchemaUtils.createMissingTablesAndColumns(TableNews)
        }
        val content = blockingDbQuery(database) {
            TableNews.select {
                TableNews.channel.eq(NewsConfig.channel)
            }.firstOrNull()?.get(TableNews.content)
        }
        if (content == null) {
            this.content = ""
            blockingDbQuery(database) {
                TableNews.insert {
                    it[this.channel] = NewsConfig.channel
                    it[this.content] = this@TilapiaLobbyPlugin.content
                }
            }
        } else {
            this.content = content
        }
        TilapiaCore.instance.addGame(TilapiaLobby(this, TilapiaCore.instance, Bukkit.getWorld("world"), "main"))
    }

    override  fun onDisable() {

    }

}