package me.fan87.plugindevkit.events;

import me.fan87.plugindevkit.PluginInstanceGrabber;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class EntityTickEvent extends EntityEvent {
    static {
        Bukkit.getScheduler().runTaskTimer(PluginInstanceGrabber.getPluginInstance(), () -> {
            for (World world : Bukkit.getServer().getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    Bukkit.getPluginManager().callEvent(new EntityTickEvent(entity));
                }
            }
        }, 0, 1);
    }

    private static final HandlerList handlers = new HandlerList();

    public EntityTickEvent(Entity what) {
        super(what);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
