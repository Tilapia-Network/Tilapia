package net.tilapiamc.ranks.metadata

import net.tilapiamc.database.DataRegistryType

abstract class RankMetaDataKey<T>(
    val registryType: DataRegistryType,
    val key: String,
    val defaultValue: T
) {

    abstract fun deserialize(value: String): T
    abstract fun serialize(value: T): String

}
