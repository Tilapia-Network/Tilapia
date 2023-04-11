package net.tilapiamc.api.commands

import net.md_5.bungee.api.ChatColor
import net.tilapiamc.api.language.LanguageKeyDelegation

object LanguageCommand {

    val COMMAND_NOT_FOUND by LanguageKeyDelegation("${ChatColor.RED}找不到該指令！請查看我們的官方文件以取得指令列表！")
    val COMMAND_ERROR by LanguageKeyDelegation("${ChatColor.RED}在執行指令時發生錯誤！請回報給伺服企管理員，這不該發生！")
    val COMMAND_INVALID_USAGE by LanguageKeyDelegation("${ChatColor.RED}錯誤的指令用法！用法: %1\$s")

}