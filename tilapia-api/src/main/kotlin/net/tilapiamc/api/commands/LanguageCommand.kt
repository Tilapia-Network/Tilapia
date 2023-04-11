package net.tilapiamc.api.commands

import net.md_5.bungee.api.ChatColor
import net.tilapiamc.api.language.LanguageKeyDelegation

object LanguageCommand {

    val COMMAND_NOT_FOUND by LanguageKeyDelegation("${ChatColor.RED}找不到該指令！請查看我們的官方文件以取得指令列表！")
    val COMMAND_ERROR by LanguageKeyDelegation("${ChatColor.RED}在執行指令時發生錯誤！請回報給伺服企管理員，這不該發生！")
    val COMMAND_INVALID_USAGE by LanguageKeyDelegation("${ChatColor.RED}錯誤的指令用法！用法: %1\$s")
    val COMMAND_PLAYER_NOT_FOUND by LanguageKeyDelegation("${ChatColor.RED}找不到名為 ${ChatColor.YELLOW}%1\$s${ChatColor.RED} 的玩家")
    val COMMAND_ENUM_NOT_FOUND_VALUE_EXPOSED by LanguageKeyDelegation("${ChatColor.RED}錯誤的參數：${ChatColor.YELLOW}%1\$s${ChatColor.RED}！可用的參數: %2\$s")
    val COMMAND_ENUM_NOT_FOUND by LanguageKeyDelegation("${ChatColor.RED}錯誤的參數: ${ChatColor.YELLOW}%1\$s${ChatColor.RED}！請查看指令用法以取得參數列表")

}