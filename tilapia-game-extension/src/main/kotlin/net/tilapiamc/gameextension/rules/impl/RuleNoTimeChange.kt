package net.tilapiamc.gameextension.rules.impl

import net.tilapiamc.spigotcommon.game.AbstractRule
import net.tilapiamc.spigotcommon.game.LocalGame

class RuleNoTimeChange(game: LocalGame): AbstractRule("NoTimeChange", game) {

    init {
        game.gameWorld.setGameRuleValue("doDaylightCycle", "false")
    }

}