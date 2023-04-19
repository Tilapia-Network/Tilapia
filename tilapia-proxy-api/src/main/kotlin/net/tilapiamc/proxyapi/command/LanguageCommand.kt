package net.tilapiamc.proxyapi.command

import net.tilapiamc.common.language.LanguageKeyDelegation

object LanguageCommand {

    val COMMAND_NOT_FOUND by LanguageKeyDelegation("&c找不到該指令！請查看我們的官方文件以取得指令列表！")
    val COMMAND_ERROR by LanguageKeyDelegation("&c在執行指令時發生錯誤！請回報給伺服企管理員，這不該發生！")
    val COMMAND_INVALID_USAGE by LanguageKeyDelegation("&c錯誤的指令用法！用法: %1\$s")
    val COMMAND_PLAYER_NOT_FOUND by LanguageKeyDelegation("&c找不到名為 &e%1\$s&c 的玩家")
    val COMMAND_GAME_NOT_FOUND by LanguageKeyDelegation("&c找不到遊戲ID為 &e%1\$s&c 的遊戲")
    val COMMAND_ENUM_NOT_FOUND_VALUE_EXPOSED by LanguageKeyDelegation("&c錯誤的參數：&e%1\$s&c！可用的參數: %2\$s")
    val COMMAND_ENUM_NOT_FOUND by LanguageKeyDelegation("&c錯誤的參數: &e%1\$s&c！請查看指令用法以取得參數列表")

    val JOIN_DENIED by LanguageKeyDelegation("&c無法將你傳送到 %1\$s ，原因: %2\$s")


}