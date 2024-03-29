package net.tilapiamc.api.commands

import net.md_5.bungee.api.ChatColor
import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.common.language.LanguageKeyDelegation

object LanguageCommand {

    val COMMAND_NOT_FOUND by LanguageKeyDelegation("${ChatColor.RED}找不到該指令！請查看我們的官方文件以取得指令列表！")
    val COMMAND_ERROR by LanguageKeyDelegation("${ChatColor.RED}在執行指令時發生錯誤！請回報給伺服企管理員，這不該發生！")
    val COMMAND_INVALID_USAGE by LanguageKeyDelegation("${ChatColor.RED}錯誤的指令用法！用法: %1\$s")
    val COMMAND_PLAYER_NOT_FOUND by LanguageKeyDelegation("${ChatColor.RED}找不到名為 ${ChatColor.YELLOW}%1\$s${ChatColor.RED} 的玩家")
    val COMMAND_GAME_NOT_FOUND by LanguageKeyDelegation("${ChatColor.RED}找不到遊戲ID為 ${ChatColor.YELLOW}%1\$s${ChatColor.RED} 的遊戲")
    val COMMAND_ENUM_NOT_FOUND_VALUE_EXPOSED by LanguageKeyDelegation("${ChatColor.RED}錯誤的參數：${ChatColor.YELLOW}%1\$s${ChatColor.RED}！可用的參數: %2\$s")
    val COMMAND_ENUM_NOT_FOUND by LanguageKeyDelegation("${ChatColor.RED}錯誤的參數: ${ChatColor.YELLOW}%1\$s${ChatColor.RED}！請查看指令用法以取得參數列表")

    val JOIN_DENIED by LanguageKeyDelegation("${ChatColor.RED}無法將你傳送到 %1\$s ，原因: %2\$s")

    init {
        TilapiaCore.instance.languageManager.registerLanguageKey(COMMAND_NOT_FOUND)
        TilapiaCore.instance.languageManager.registerLanguageKey(COMMAND_ERROR)
        TilapiaCore.instance.languageManager.registerLanguageKey(COMMAND_INVALID_USAGE)
        TilapiaCore.instance.languageManager.registerLanguageKey(COMMAND_PLAYER_NOT_FOUND)
        TilapiaCore.instance.languageManager.registerLanguageKey(COMMAND_GAME_NOT_FOUND)
        TilapiaCore.instance.languageManager.registerLanguageKey(COMMAND_ENUM_NOT_FOUND_VALUE_EXPOSED)
        TilapiaCore.instance.languageManager.registerLanguageKey(COMMAND_ENUM_NOT_FOUND)
        TilapiaCore.instance.languageManager.registerLanguageKey(JOIN_DENIED)
    }

}