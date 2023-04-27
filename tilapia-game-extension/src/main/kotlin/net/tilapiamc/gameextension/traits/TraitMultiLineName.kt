package net.tilapiamc.gameextension.traits

import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.npc.MemoryNPCDataStore
import net.citizensnpcs.api.npc.NPC
import net.citizensnpcs.api.trait.Trait
import net.citizensnpcs.trait.ArmorStandTrait
import net.citizensnpcs.util.NMS
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.event.player.PlayerTeleportEvent

class TraitMultiLineName(val lineMargin: Double, val textPosition: Double): Trait("multiline-name") {

    /**
     * From up to down
     */
    val lines = ArrayList<String>()
    val npcs = ArrayList<NPC>()
    val registry = CitizensAPI.createCitizensBackedNPCRegistry(MemoryNPCDataStore())

    override fun onAttach() {
        updateLines()
    }

    override fun onDespawn() {
        onDespawn()
    }

    override fun onPreSpawn() {
        onAttach()
    }

    override fun onRemove() {
        for (npc in npcs) {
            npc.destroy()
        }
    }

    override fun run() {
        for (withIndex in npcs.withIndex()) {
            withIndex.value.teleport(this.npc.entity.location.add(0.0, (withIndex.index) * lineMargin + textPosition + NMS.getHeight(this.npc.entity), 0.0), PlayerTeleportEvent.TeleportCause.UNKNOWN)
        }
    }

    private fun updateLines() {
        if (npcs.size < lines.size) {
            val baseIndex = npcs.size
            repeat(lines.size - npcs.size) {
                this.npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, false)
                val npc = registry.createNPC(EntityType.ARMOR_STAND, lines[lines.size - 1 - (it + baseIndex)])
                npc.spawn(this.npc.entity.location.add(0.0, (it + baseIndex) * lineMargin + textPosition + NMS.getHeight(this.npc.entity), 0.0))
                npc.getOrAddTrait(ArmorStandTrait::class.java).apply {
                    gravity = false
                    isVisible = false
                    isMarker = true
                }
                npcs.add(npc)
            }
        }
        for (entry in ArrayList(npcs).withIndex()) {
            val index = entry.index
            val npc = entry.value
            if (index >= lines.size) {
                npc.destroy()
                npcs.remove(npc)
                continue
            }
            if (npc.name != lines[lines.size - 1 - index]) {
                npc.name = lines[lines.size - 1 - index]
            }
        }
    }

}