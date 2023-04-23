package net.tilapiamc.api.events.server

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class ServerShutdownEvent: Event() {
    companion object {
        val handlerList = HandlerList()
    }

    override fun getHandlers() = handlerList
}