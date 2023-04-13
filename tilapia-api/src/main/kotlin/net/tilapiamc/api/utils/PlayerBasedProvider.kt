package net.tilapiamc.api.utils

import org.bukkit.entity.Player

abstract class PlayerBasedProvider<T> {

    abstract operator fun invoke(player: Player): T

    abstract fun onJoin(player: Player)
    abstract fun onQuit(player: Player)

}