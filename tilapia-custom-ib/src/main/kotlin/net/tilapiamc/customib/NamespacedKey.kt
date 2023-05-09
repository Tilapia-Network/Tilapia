package net.tilapiamc.customib

import net.minecraft.server.v1_8_R3.NBTBase
import net.minecraft.server.v1_8_R3.NBTTagCompound

data class NamespacedKey(val namespace: String, val key: String) {

    fun writeToNBT(tag: NBTTagCompound) {
        tag.setString("Namespace", namespace)
        tag.setString("ID", key)
    }

    companion object {
        fun readFromNBT(tag: NBTTagCompound): NamespacedKey? {
            if (!tag.hasKeyOfType("Namespace", NBTBase.a.indexOf("STRING"))) return null
            if (!tag.hasKeyOfType("ID", NBTBase.a.indexOf("STRING"))) return null
            return NamespacedKey(tag.getString("Namespace"), tag.getString("ID"))
        }
    }

    override fun toString(): String {
        return "$namespace:$key"
    }
}