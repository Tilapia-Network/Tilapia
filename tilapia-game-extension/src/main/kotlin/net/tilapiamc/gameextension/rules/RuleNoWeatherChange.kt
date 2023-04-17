package net.tilapiamc.gameextension.rules

import net.tilapiamc.common.events.annotation.Subscribe
import net.tilapiamc.spigotcommon.game.AbstractRule
import net.tilapiamc.spigotcommon.game.LocalGame
import org.bukkit.event.weather.WeatherChangeEvent

class RuleNoWeatherChange(game: LocalGame): AbstractRule("NoWeatherChange", game) {

    init {
    }

    @Subscribe("noWeatherChange-onWeatherChange")
    fun onWeatherChange(event: WeatherChangeEvent) {
        event.isCancelled = true
    }

}