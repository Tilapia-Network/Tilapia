package net.tilapiamc.customib.item

import net.minecraft.server.v1_8_R3.ItemStack
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata
import net.minecraft.server.v1_8_R3.PacketPlayOutWindowItems
import net.tilapiamc.api.events.packet.PacketSendEvent
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.api.player.PlayersManager.getLocalPlayer
import net.tilapiamc.common.events.annotation.Subscribe
import net.tilapiamc.customib.NamespacedKey
import net.tilapiamc.customib.utils.NMSUtils.getCustomItemTagMirror
import net.tilapiamc.customib.utils.NMSUtils.toBukkitMirror
import net.tilapiamc.customib.utils.NMSUtils.toNMSCopy
import net.tilapiamc.spigotcommon.game.event.GameEventManager
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack

class ItemsManager(val eventManager: GameEventManager) {

    init {
        eventManager.registerListener(this)
    }

    private val _customItems = HashMap<NamespacedKey, CustomItem>()
    val customItems: Map<NamespacedKey, CustomItem>
        get() = _customItems

    fun registerItem(item: CustomItem) {
        _customItems[item.key] = item
        eventManager.registerListener(item)
    }

    // Item display name & lore translation
    @Subscribe("itemsManager-translateItem")
    fun translateItem(event: PacketSendEvent) {
        val packet = event.original
        for (declaredField in packet.packet.handle.javaClass.declaredFields) {
            declaredField.isAccessible = true
            val value = declaredField[packet.packet.handle] ?: continue
            // ItemStack array
            if (declaredField.type.isArray && declaredField.type.componentType == ItemStack::class.java) {
                val value = value as Array<ItemStack>
                for (withIndex in value.withIndex()) {
                    if (withIndex.value == null) continue
                    value[withIndex.index] = processItemStack(event.player.getLocalPlayer(), withIndex.value)
                }
            }
            // ItemStack list
            if (value is List<*>) {
                val value = value as MutableList<Any>
                ArrayList(value).withIndex().forEach {
                    val item = it.value
                    val index = it.index
                    if (item is ItemStack) {
                        value[index] = processItemStack(event.player.getLocalPlayer(), item)
                    }
                }
            }
            // ItemStack
            if (value is ItemStack) {
                declaredField[packet.packet.handle] = processItemStack(event.player.getLocalPlayer(), value)
            }
            // TODO: Data watcher
        }
    }

    fun processItemStack(player: LocalNetworkPlayer, itemStack: ItemStack): ItemStack {
        val customTag = itemStack.getCustomItemTagMirror() ?: return itemStack
        val namespacedKey = NamespacedKey.readFromNBT(customTag) ?: return itemStack
        val customItem = customItems.values.firstOrNull { it.key == namespacedKey } ?: return itemStack
        return customItem.getDisplayItem(player, itemStack.toBukkitMirror()).toNMSCopy()
    }

}