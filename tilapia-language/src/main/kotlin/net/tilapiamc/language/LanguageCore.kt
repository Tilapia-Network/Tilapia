package net.tilapiamc.language

import net.md_5.bungee.api.ChatColor
import net.tilapiamc.api.language.LanguageKeyDelegation

object LanguageCore {

    val INVALID_JOIN_NO_GAME by LanguageKeyDelegation("${ChatColor.RED}找不到你可以加入的遊戲！")
    val SEND_TO_A_GAME by LanguageKeyDelegation("${ChatColor.DARK_GRAY}正在傳送你至 ${ChatColor.GRAY}%1\$s${ChatColor.DARK_GRAY}...")
    val COULD_NOT_FIND_GAME by LanguageKeyDelegation("${ChatColor.RED}找不到可以加入的遊戲！請稍後再重試一次")
    val COULD_NOT_FIND_LOBBY by LanguageKeyDelegation("${ChatColor.RED}找不到可用的大廳！請稍後再重試一次")

    // Commands
    val COMMAND_GAMEMODE_SUCCESS by LanguageKeyDelegation("${ChatColor.GREEN}成功將你的遊戲模式調為 ${ChatColor.YELLOW}%1\$s ${ChatColor.GREEN}!")

    // Temporary Language
    val TEMP_GAME_STOPPED by LanguageKeyDelegation("${ChatColor.RED}TODO: 伺服器以結束，且找不到可加入的備用伺服器\n請立刻回報此錯誤給管理員，這不該發生")
}