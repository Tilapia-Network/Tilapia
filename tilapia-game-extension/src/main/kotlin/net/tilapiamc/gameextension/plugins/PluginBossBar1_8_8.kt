package net.tilapiamc.gameextension.plugins

import me.fan87.plugindevkit.events.EntityTickEvent
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.npc.MemoryNPCDataStore
import net.citizensnpcs.api.npc.NPC
import net.citizensnpcs.api.trait.trait.PlayerFilter
import net.tilapiamc.api.events.game.PlayerJoinGameEvent
import net.tilapiamc.api.events.game.PlayerQuitGameEvent
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.common.events.annotation.Subscribe
import net.tilapiamc.spigotcommon.game.plugin.GamePlugin
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent

class PluginBossBar1_8_8(val getBossBarText: (LocalNetworkPlayer) -> String?): GamePlugin() {

    companion object {
        val registry = CitizensAPI.createNamedNPCRegistry("bossbar", MemoryNPCDataStore())
    }

    val bossBarForPlayer = HashMap<Player, NPC>()

    override fun onEnable() {
        eventManager.registerListener(this)
    }

    override fun onDisable() {
    }

    @Subscribe("bossBar-onPlayerJoin")
    fun onPlayerJoin(event: PlayerJoinGameEvent) {
    }

    @Subscribe("bossBar-onPlayerTick")
    fun onPlayerTick(event: EntityTickEvent) {
        val player = event.entity
        if (player !is Player) return
        try {
            val text = getBossBarText(player.getLocalPlayer())
            if (text != null) {
                if (bossBarForPlayer[player] != null) {
                    bossBarForPlayer[player]!!.name = text
                    (bossBarForPlayer[player]!!.entity as CraftEntity).handle.isInvisible = true
                    bossBarForPlayer[player]!!.teleport(getNPCLocation(player), PlayerTeleportEvent.TeleportCause.PLUGIN)
                } else {
                    val npc = createNPC(player)
                    bossBarForPlayer[player] = npc
                    npc.name = text
                }
            }
        } catch (e: NullPointerException) {}
    }

    @Subscribe("bossBar-onPlayerQuit")
    fun onPlayerQuit(event: PlayerQuitGameEvent) {
        bossBarForPlayer[event.player.bukkitPlayer]?.destroy()
        bossBarForPlayer.remove(event.player)
    }

    fun createNPC(player: Player): NPC {
        val npc = registry.createNPC(EntityType.WITHER, "Bossbar for ${player.name}")
        npc.isProtected = true
        npc.addTrait(PlayerFilter().also {
            it.only(player.uniqueId)
        })
        npc.data().setPersistent(NPC.Metadata.SHOULD_SAVE, false)
        npc.data().setPersistent(NPC.Metadata.SILENT, true)
        npc.spawn(getNPCLocation(player))

        return npc
    }

    fun getNPCLocation(player: Player): Location {
        val vec = player.location.toVector()
        vec.add(player.location.direction.multiply(20))
        return Location(player.world, vec.x, vec.y, vec.z)
    }



}