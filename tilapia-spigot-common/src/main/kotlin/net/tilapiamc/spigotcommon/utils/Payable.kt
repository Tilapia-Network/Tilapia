package net.tilapiamc.spigotcommon.utils

import org.bukkit.entity.Player

abstract class Payable<T: Number>(val player: Player) {

    abstract fun pay(amount: T): Boolean
    abstract fun getAmount(): T


}