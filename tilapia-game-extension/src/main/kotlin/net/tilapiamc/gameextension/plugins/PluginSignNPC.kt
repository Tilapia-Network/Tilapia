package net.tilapiamc.gameextension.plugins

import me.fan87.plugindevkit.events.ServerTickEvent
import net.citizensnpcs.Citizens
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.npc.MemoryNPCDataStore
import net.citizensnpcs.api.npc.NPC
import net.citizensnpcs.api.npc.NPCRegistry
import net.tilapiamc.common.EventTarget
import net.tilapiamc.common.events.annotation.Subscribe
import net.tilapiamc.spigotcommon.game.plugin.GamePlugin
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.Sign
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockEvent
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.world.ChunkLoadEvent

class PluginSignNPC(val sandbox: Boolean,
                    val signFilter: (lines: Array<String>) -> Boolean,
                    val npcFactory: (sign: Sign, registry: NPCRegistry, pluginSignNpc: PluginSignNPC) -> NPC): GamePlugin() {

    val npcs = HashMap<Location, NPC>()

    val onNpcDestroyed = EventTarget<NPC>()
    val onNpcCreated = EventTarget<NPC>()
    val registry = CitizensAPI.createAnonymousNPCRegistry(MemoryNPCDataStore())

    override fun onEnable() {
        eventManager.registerListener(this)
        for (loadedChunk in game.gameWorld.loadedChunks) {
            for (tileEntity in loadedChunk.tileEntities) {
                val sign = tileEntity.block.state
                if (sign is Sign) {
                    if (signFilter(sign.lines)) {
                        destroyNpc(tileEntity.block)
                        createNpc(sign)
                    } else {
                        destroyNpc(tileEntity.block)
                    }
                }
            }
        }

    }

    override fun onDisable() {
        for (value in npcs.values) {
            value.destroy()
        }
        registry.deregisterAll()
    }

    @Subscribe("pluginSignNPC-onSignLoaded")
    fun onSignLoaded(event: ChunkLoadEvent) {
        for (tileEntity in event.chunk.tileEntities) {
            val sign = tileEntity.block.state
            if (sign is Sign) {
                if (signFilter(sign.lines)) {
                    destroyNpc(tileEntity.block)
                    createNpc(sign)
                } else {
                    destroyNpc(tileEntity.block)
                }
            }
        }
    }


    @Subscribe("pluginSignNPC-onSignDestroyed")
    fun onSignDestroyed(event: BlockBreakEvent) {
        val sign = event.block.state
        if (sign is Sign) {
            destroyNpc(event.block)
        }
    }
    @Subscribe("pluginSignNPC-onPhysics")
    fun onPhysics(event: BlockPhysicsEvent) {
        if (event.changedType == Material.AIR && event.block.state is Sign) {
            val sign = event.block.state as Sign
            destroyNpc(event.block)
        }
    }

    @Subscribe("pluginSignNPC-onSignPlaced")
    fun onSignPlaced(event: SignChangeEvent) {
        val sign = event.block.state as Sign
        if (signFilter(event.lines)) {
            destroyNpc(event.block)
            createNpc(sign)
        } else {
            destroyNpc(event.block)
        }
    }

    fun createNpc(sign: Sign) {
        val createdNpc = npcFactory(sign, registry, this)
        npcs[sign.location] = createdNpc
        onNpcCreated(createdNpc)
        if (!sandbox) {
            sign.block.setType(Material.AIR, false)
        }
    }
    fun destroyNpc(sign: Block) {
        val npc = npcs[sign.location]
        if (npc != null) {
            onNpcDestroyed(npc)
            npc.destroy()
            npcs.remove(sign.location)
        }
    }

}