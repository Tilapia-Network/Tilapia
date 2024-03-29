package net.tilapiamc.language

import net.md_5.bungee.api.ChatColor
import net.tilapiamc.api.TilapiaCore
import net.tilapiamc.common.language.LanguageKeyDelegation

object LanguageGame {

    val WAITING_PLAYER_JOIN by LanguageKeyDelegation("${ChatColor.GREEN}[+] ${ChatColor.YELLOW}%1\$s 加入了遊戲 (%2\$d/%3\$d)")
    val WAITING_PLAYER_QUIT by LanguageKeyDelegation("${ChatColor.RED}[-] ${ChatColor.YELLOW}%1\$s 退出了遊戲 (%2\$d/%3\$d)")

    val WAITING_WAITING_MSG by LanguageKeyDelegation("${ChatColor.RED}正在等待更多玩家...")
    val WAITING_COUNTDOWN_MSG by LanguageKeyDelegation("${ChatColor.YELLOW}遊戲將在%1\$d秒後開始")
    val WAITING_WAITING by LanguageKeyDelegation("${ChatColor.RED}正在等待更多玩家...")
    val WAITING_COUNTDOWN by LanguageKeyDelegation("${ChatColor.YELLOW}遊戲將在%1\$d秒後開始")

    val SPECTATOR_PREFIX by LanguageKeyDelegation("${ChatColor.GRAY}[觀眾] ")

    init {
        TilapiaCore.instance.languageManager.registerLanguageKey(WAITING_PLAYER_JOIN)
        TilapiaCore.instance.languageManager.registerLanguageKey(WAITING_PLAYER_QUIT)
        TilapiaCore.instance.languageManager.registerLanguageKey(WAITING_WAITING_MSG)
        TilapiaCore.instance.languageManager.registerLanguageKey(WAITING_COUNTDOWN_MSG)
        TilapiaCore.instance.languageManager.registerLanguageKey(WAITING_WAITING)
        TilapiaCore.instance.languageManager.registerLanguageKey(WAITING_COUNTDOWN)
        TilapiaCore.instance.languageManager.registerLanguageKey(SPECTATOR_PREFIX)
    }

}