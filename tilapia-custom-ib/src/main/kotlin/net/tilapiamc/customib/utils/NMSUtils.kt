package net.tilapiamc.customib.utils

import net.minecraft.server.v1_8_R3.NBTBase
import net.minecraft.server.v1_8_R3.NBTTagCompound
import net.tilapiamc.customib.NBTKeyRegistry
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack

object NMSUtils {

    fun ItemStack.toNMSCopy(): net.minecraft.server.v1_8_R3.ItemStack = CraftItemStack.asNMSCopy(this)
    fun net.minecraft.server.v1_8_R3.ItemStack.toBukkitMirror(): ItemStack = CraftItemStack.asCraftMirror(this)

    private val tagField = net.minecraft.server.v1_8_R3.ItemStack::class.java.getDeclaredField("tag").also {
        it.isAccessible = true
    }

    fun net.minecraft.server.v1_8_R3.ItemStack.getNBTTagMirror(): NBTTagCompound {
        return (tagField[this] as NBTTagCompound?)?:NBTTagCompound().also {
            setNBTTag(it)
        }
    }

    fun net.minecraft.server.v1_8_R3.ItemStack.setNBTTag(tag: NBTTagCompound) {
        tagField[this] = tag
    }

    fun net.minecraft.server.v1_8_R3.ItemStack.getCustomItemTagMirror(): NBTTagCompound? {
        if (!getNBTTagMirror().hasKeyOfType(NBTKeyRegistry.CUSTOM_ITEM_DATA, NBTBase.a.indexOf("COMPOUND"))) {
            return null
        }
        return getNBTTagMirror().getCompound(NBTKeyRegistry.CUSTOM_ITEM_DATA)
    }

}