package net.tilapiamc.fleetwars

import me.fan87.plugindevkit.gui.Gui
import me.fan87.plugindevkit.gui.GuiItem
import me.fan87.plugindevkit.utils.ItemStackBuilder
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.common.language.LanguageKey
import net.tilapiamc.common.language.LanguageKeyDelegation
import net.tilapiamc.customib.NamespacedKey
import net.tilapiamc.customib.item.CustomItem
import net.tilapiamc.customib.item.ItemsManager
import net.tilapiamc.spigotcommon.utils.ItemPayable
import net.tilapiamc.spigotcommon.utils.Payable
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.inventory.ItemStack

class GuiShop(val player: LocalNetworkPlayer, val itemsManager: ItemsManager): Gui(player.getLanguageBundle()[FLEETWARS_SHOP_TITLE], 6) {

    companion object {
        val FLEETWARS_SHOP_CLICK_TO_PURCHASE by LanguageKeyDelegation("${ChatColor.YELLOW}點我購買")
        val FLEETWARS_SHOP_PURCHASE_SUCCESS by LanguageKeyDelegation("${ChatColor.GREEN}成功購買")
        val FLEETWARS_SHOP_TITLE by LanguageKeyDelegation("商店")
        val FLEETWARS_SHOP_INVENTORY_FULL by LanguageKeyDelegation("${ChatColor.RED}你的物品欄是滿的！")
        val FLEETWARS_SHOP_NOT_ENOUGH_COIN by LanguageKeyDelegation("${ChatColor.RED}你並沒有足夠的資源購買這個物品！")
        val FLEETWARS_SHOP_CLOSE by LanguageKeyDelegation("${ChatColor.RED}關閉")

        fun registerLanguageKeys() {
            CoinType.GOLD
        }
    }

    fun getPayable(coinType: CoinType): Payable<Int> {
        return ItemPayable(player, coinType.filter)
    }

    override fun init() {
        fillBorder(GuiItem(ItemStackBuilder(Material.STAINED_GLASS_PANE, 7)
            .setDisplayName(" ")
            .build()))
        set(5, 6, GuiItem(ItemStackBuilder(Material.BARRIER)
            .setDisplayName(player.getLanguageBundle()[FLEETWARS_SHOP_CLOSE])
            .build()) {
            it.whoClicked.closeInventory()
        })

        set(2, 2, ShopItem(itemsManager.customItems[NamespacedKey("fleetwars", "fireball")]!!, 1, CoinType.GOLD, 32))
    }

    private inner class ShopItem(val targetItem: ItemStack, val itemLanguageKey: LanguageKey, val coinType: CoinType, val price: Int): GuiItem(
        ItemStackBuilder(targetItem.clone())
            .setDisplayName("${ChatColor.GREEN}${player.getLanguageBundle()[itemLanguageKey]}")
            .addLore("${player.getLanguageBundle()[coinType.displayName]} ${ChatColor.GRAY}x$price")
            .addLore("")
            .addLore(player.getLanguageBundle()[FLEETWARS_SHOP_CLICK_TO_PURCHASE])
            .build(),
        lambda@{
            if (player.inventory.firstEmpty() == -1) {
                player.playSound(player.location, Sound.ENDERMAN_TELEPORT, 1f, 1f)
                player.sendMessage(player.getLanguageBundle()[FLEETWARS_SHOP_INVENTORY_FULL])
                return@lambda
            }
            if (!getPayable(coinType).pay(price)) {
                player.playSound(player.location, Sound.ENDERMAN_TELEPORT, 1f, 1f)
                player.sendMessage(player.getLanguageBundle()[FLEETWARS_SHOP_NOT_ENOUGH_COIN])
                return@lambda
            }
            player.inventory.addItem(targetItem.clone())
            player.playSound(player.location, Sound.NOTE_PIANO, 1f, 1f)
            player.sendMessage(player.getLanguageBundle()[FLEETWARS_SHOP_PURCHASE_SUCCESS])
        }
    ) {

        constructor(customItem: CustomItem, amount: Int, coinType: CoinType, price: Int): this(
            customItem.generateItem(amount),
            customItem.displayLanguageKey,
            coinType,
            price
        )
    }

}