package net.tilapiamc.api.utils

import org.bukkit.entity.Player
import java.util.UUID

class HashMapPlayerProvider<T>(val defaultValue: T): PlayerBasedProvider<T>() {
    private val cache = HashMap<UUID, T>()
    operator fun set(key: Player, value: T) {
        cache[key.uniqueId] = value
    }
    operator fun get(key: Player): T {
        return cache[key.uniqueId]?:defaultValue
    }
    override fun invoke(player: Player): T {
        return this[player]
    }

    override fun onJoin(player: Player) {

    }

    override fun onQuit(player: Player) {
        cache.remove(player.uniqueId)
    }
}