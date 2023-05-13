package net.tilapiamc.customib.block

import net.minecraft.server.v1_8_R3.NBTTagCompound
import net.tilapiamc.common.language.LanguageKey
import net.tilapiamc.common.language.LanguageManager
import net.tilapiamc.customib.NamespacedKey
import net.tilapiamc.customib.item.CustomItem
import org.bukkit.Material
import org.bukkit.block.Block

abstract class CustomBlock(val key: NamespacedKey, val displayName: String) {

    abstract val material: Material
    abstract val damage: Short

    abstract fun setBlockType(block: Block)

    fun getBlockLanguageKey(name: String, defaultValue: String): LanguageKey {
        val key = LanguageKey("CUSTOM_BLOCK_${key.namespace}_${key.key}_$name", defaultValue)
        LanguageManager.instance.registerLanguageKey(key)
        return key
    }


    fun applyBlockNBT(nbt: NBTTagCompound) {
        // Basic NBT
        key.writeToNBT(nbt)
    }

    // TODO: Add a "validateBlockType"


}