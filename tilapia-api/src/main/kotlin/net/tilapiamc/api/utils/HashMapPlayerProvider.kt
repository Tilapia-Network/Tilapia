package net.tilapiamc.api.utils

import org.bukkit.entity.Player

class HashMapPlayerProvider<T>(val defaultValue: T): PlayerBasedProvider<T>() {
    private val cache = HashMap<Player, T>()
    operator fun set(key: Player, value: T) {
        cache[key] = value
    }
    operator fun get(key: Player): T {
        return cache[key]?:defaultValue
    }
    override fun invoke(player: Player): T {
        return this[player]
    }

    override fun onJoin(player: Player) {

    }

    override fun onQuit(player: Player) {
        cache.remove(player)
    }
}