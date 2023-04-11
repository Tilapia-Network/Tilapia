package net.tilapiamc.gameextension.plugins

import com.comphenix.protocol.PacketType
import me.fan87.plugindevkit.events.EntityTickEvent
import net.minecraft.server.v1_8_R3.EntityBoat
import net.tilapiamc.api.events.annotation.Subscribe
import net.tilapiamc.api.events.game.PlayerJoinGameEvent
import net.tilapiamc.api.events.game.PlayerQuitGameEvent
import net.tilapiamc.api.events.packet.PacketReceiveEvent
import net.tilapiamc.api.hook.MethodTransformer
import net.tilapiamc.api.hook.TransformManager
import net.tilapiamc.api.utils.ASMUtils
import net.tilapiamc.api.utils.LocalVarManager
import net.tilapiamc.spigotcommon.game.plugin.GamePlugin
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Boat
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.vehicle.VehicleDamageEvent
import org.bukkit.event.vehicle.VehicleDestroyEvent
import org.bukkit.event.vehicle.VehicleEnterEvent
import org.bukkit.event.vehicle.VehicleExitEvent
import org.bukkit.util.Vector
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode
import java.util.UUID
import kotlin.math.cos
import kotlin.math.sin
import kotlin.reflect.jvm.javaMethod

class PluginNoBoatCrash: GamePlugin() {

    companion object {


    }

    override fun onEnable() {
        eventManager.registerListener(this)
    }

    override fun onDisable() {

    }


    @Subscribe("noBoatCrash-onVehicleDestroy")
    fun onVehicleDestroy(event: VehicleDestroyEvent) {
        if (event.vehicle is Boat && event.attacker == null) {
            event.isCancelled = true
        }
    }

}

