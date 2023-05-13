package net.tilapiamc.fleetwars

import net.citizensnpcs.api.event.NPCLeftClickEvent
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.api.trait.Trait
import net.citizensnpcs.trait.Gravity
import net.tilapiamc.api.events.EventsManager
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.common.events.annotation.Subscribe
import net.tilapiamc.common.events.annotation.registerAnnotationBasedListener
import net.tilapiamc.common.events.annotation.unregisterAnnotationBasedListener
import net.tilapiamc.customib.item.ItemsManager
import net.tilapiamc.customib.PluginCustomItemBlocks
import net.tilapiamc.customib.block.BlocksManager
import net.tilapiamc.customib.block.CustomBlock
import net.tilapiamc.customib.item.CustomItem
import net.tilapiamc.fleetwars.customitems.BlockSpeedPad
import net.tilapiamc.fleetwars.customitems.ItemFireBall
import net.tilapiamc.gameextension.plugins.PluginSignNPC
import net.tilapiamc.gameextension.traits.TraitMultiLineName
import net.tilapiamc.sandbox.TilapiaSandbox
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.EntityType
import org.bukkit.material.Sign
import kotlin.math.atan2

object FleetWarsRules {

    private val itemProviders: Array<(Boolean) -> CustomItem> = arrayOf(
        { ItemFireBall(it) }
    )

    private val blockProviders: Array<(Boolean, BlocksManager) -> CustomBlock> = arrayOf(
        { _, blocksManager -> BlockSpeedPad(blocksManager) }
    )

    fun makeFleetWarsSandbox(sandbox: TilapiaSandbox) {
        val plugin = PluginCustomItemBlocks {
            for (itemProvider in itemProviders) {
                itemsManager.registerItem(itemProvider(true))
            }
            for (blockProvider in blockProviders) {
                blocksManager.registerBlock(blockProvider(true, blocksManager))
            }
        }
        sandbox.applyPlugin(plugin)
        sandbox.applyPlugin(provideShopNPCPlugin(true, plugin.itemsManager))
    }
    fun makeFleetWars(fleetWars: FleetWars) {
        val plugin = PluginCustomItemBlocks {
            for (itemProvider in itemProviders) {
                itemsManager.registerItem(itemProvider(false))
            }
            for (blockProvider in blockProviders) {
                blocksManager.registerBlock(blockProvider(false, blocksManager))
            }
        }
        fleetWars.applyPlugin(plugin)
        fleetWars.applyPlugin(provideShopNPCPlugin(false, plugin.itemsManager))
    }

    private fun provideShopNPCPlugin(sandbox: Boolean, itemsManager: ItemsManager): PluginSignNPC {
        class TraitShopNPC(val sign: Block, val pluginSignNPC: PluginSignNPC): Trait("shopnpc") {
            override fun run() {
                if (npc.entity.ticksLived > 10) {
                    if (sign.state.data !is Sign) {
                        pluginSignNPC.destroyNpc(sign)
                    }
                }
            }

            override fun onAttach() {
                EventsManager.registerAnnotationBasedListener(this)
            }

            override fun onRemove() {
                EventsManager.unregisterAnnotationBasedListener(this)
            }
            override fun onSpawn() {
                EventsManager.registerAnnotationBasedListener(this)
            }

            override fun onDespawn() {
                EventsManager.unregisterAnnotationBasedListener(this)
            }

            @Subscribe("shopNpc-onRightClick")
            fun onRightClick(event: NPCRightClickEvent) {
                if (event.npc == npc) {
                    GuiShop(event.clicker.getLocalPlayer(), itemsManager).open(event.clicker)
                    event.isCancelled = true
                }
            }
            @Subscribe("shopNpc-onLeftClick")
            fun onLeftClick(event: NPCLeftClickEvent) {
                if (event.npc == npc && sandbox) {
                    sign.type = Material.AIR
                    pluginSignNPC.destroyNpc(sign)
                }
            }

        }
        return PluginSignNPC(sandbox, { it[0] == "__shop__" }) { sign, registry, pluginSignNpc ->
            val npc = registry.createNPC(EntityType.VILLAGER, "")
            npc.isProtected = true
            npc.isFlyable = false
            npc.spawn(sign.location.add(0.5, 0.0, 0.5).also {
                val face = (sign.data as Sign).facing.oppositeFace
                it.yaw = Math.toDegrees(atan2(face.modX.toDouble(), (sign.data as Sign).facing.modZ.toDouble())).toFloat()
            })
            npc.getOrAddTrait(Gravity::class.java).gravitate(true)
            npc.addTrait(TraitMultiLineName(0.27, 0.1).apply {
                lines.add("${ChatColor.GREEN}商店")
                lines.add("${ChatColor.YELLOW}點我開啟")
            })
            npc.addTrait(TraitShopNPC(sign.block, pluginSignNpc))
            npc
        }
    }

}