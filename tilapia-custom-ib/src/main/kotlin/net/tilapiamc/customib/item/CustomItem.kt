package net.tilapiamc.customib.item

import me.fan87.plugindevkit.utils.ItemStackBuilder
import net.minecraft.server.v1_8_R3.NBTTagCompound
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.common.language.LanguageKey
import net.tilapiamc.common.language.LanguageManager
import net.tilapiamc.customib.NBTKeyRegistry
import net.tilapiamc.customib.NamespacedKey
import net.tilapiamc.customib.utils.NMSUtils.getCustomItemTagMirror
import net.tilapiamc.customib.utils.NMSUtils.getNBTTagMirror
import net.tilapiamc.customib.utils.NMSUtils.toBukkitMirror
import net.tilapiamc.customib.utils.NMSUtils.toNMSCopy
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData

abstract class CustomItem(val key: NamespacedKey, val displayName: String) {

    abstract val material: Material
    abstract val damage: Short

    protected abstract fun applyDisplays(player: LocalNetworkPlayer, itemStack: ItemStack): ItemStack
    protected abstract fun applyExtraNBT(nbt: NBTTagCompound)

    val displayLanguageKey: LanguageKey by lazy {
        getItemLanguageKey("DISPLAY_NAME", displayName)
    }

    init {
        displayLanguageKey
    }

    fun getItemLanguageKey(name: String, defaultValue: String): LanguageKey {
        val key = LanguageKey("CUSTOM_ITEM_${key.namespace}_${key.key}_$name", defaultValue)
        LanguageManager.instance.registerLanguageKey(key)
        return key
    }

    fun applyNBT(nbt: NBTTagCompound) {
        val nbtTagCompound = NBTTagCompound()

        // Basic NBT
        key.writeToNBT(nbtTagCompound)

        applyExtraNBT(nbtTagCompound)
        nbt.set(NBTKeyRegistry.CUSTOM_ITEM_DATA, nbtTagCompound)
    }

    private fun postProcess(itemStack: ItemStack): ItemStack {
        val nmsItem = itemStack.toNMSCopy()
        nmsItem.isStackable
        val tag = nmsItem.getNBTTagMirror()
        applyNBT(tag)
        val out = nmsItem.toBukkitMirror()
        val itemMeta = out.itemMeta
        itemMeta?.displayName = key.key
        out.itemMeta = itemMeta
        return out
    }

    fun generateItem(amount: Int): ItemStack {
        val out = ItemStack(material)
        out.amount = amount
        out.durability = damage
        return postProcess(out)
    }


    fun isAnItemOf(itemStack: ItemStack?): Boolean {
        if (itemStack == null) return false
        if (itemStack.type != material) return false
        val itemTag = itemStack.toNMSCopy().getCustomItemTagMirror() ?: return false
        val namespacedKey = NamespacedKey.readFromNBT(itemTag)
        return namespacedKey == this.key
    }

    fun getDisplayItem(player: LocalNetworkPlayer, itemStack: ItemStack): ItemStack {
        val cloned = ItemStackBuilder(itemStack.clone())
            .setDisplayName(player.getLanguageBundle()[displayLanguageKey])
        return applyDisplays(player, cloned.build())
    }

}