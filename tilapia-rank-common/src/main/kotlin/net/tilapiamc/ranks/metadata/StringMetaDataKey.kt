package net.tilapiamc.ranks.metadata

import net.tilapiamc.database.DataRegistryType

class StringMetaDataKey(registryType: DataRegistryType,
                        key: String,
                        defaultValue: String) : RankMetaDataKey<String>(
    registryType, key, defaultValue
) {
    override fun deserialize(value: String): String {
        return value
    }

    override fun serialize(value: String): String {
        return value
    }
}