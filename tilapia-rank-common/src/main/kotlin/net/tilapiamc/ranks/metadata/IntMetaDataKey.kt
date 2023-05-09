package net.tilapiamc.ranks.metadata

import net.tilapiamc.database.DataRegistryType

class IntMetaDataKey(registryType: DataRegistryType,
                     key: String,
                     defaultValue: Int) : RankMetaDataKey<Int>(
    registryType, key, defaultValue
) {
    override fun deserialize(value: String): Int {
        return value.toInt()
    }

    override fun serialize(value: Int): String {
        return value.toString()
    }
}