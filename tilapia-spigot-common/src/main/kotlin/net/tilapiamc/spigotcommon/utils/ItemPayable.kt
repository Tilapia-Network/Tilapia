package net.tilapiamc.spigotcommon.utils

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ItemPayable(player: Player, val itemFilter: (ItemStack) -> Boolean): Payable<Int>(player) {
    override fun pay(amount: Int): Boolean {
        if (getAmount() < amount) {
            return false
        }
        var requireAmount = amount
        for (content in player.inventory.contents.withIndex().filter {it.value != null}) {
            val item = content.value
            if (itemFilter(item)) {
                val reducedAmount = requireAmount.coerceAtMost(item.amount)
                item.amount -= reducedAmount
                player.inventory.setItem(content.index, item)
                requireAmount -= reducedAmount
            }
            if (requireAmount == 0) {
                break
            }
        }
        if (requireAmount > 0) {
            throw IllegalArgumentException("Item Filter inconsistency")
        }
        return true
    }

    override fun getAmount(): Int {
        return player.inventory.contents.filterNotNull().filter(itemFilter).sumOf { it.amount }
    }


}