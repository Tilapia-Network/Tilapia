package me.fan87.plugindevkit.events;

import me.fan87.plugindevkit.PluginInstanceGrabber;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerTickEvent extends Event {
    static {
        Bukkit.getScheduler().runTaskTimer(PluginInstanceGrabber.getPluginInstance(), () -> {
            Bukkit.getPluginManager().callEvent(new ServerTickEvent());
        }, 0, 1);
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
