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

private val boatMaxSpeed = HashMap<Any, Double>()

// This only works in 1.8
class PluginSpeedyBoat_1_8(val instantStop: Boolean, val speedFunc: (Boat) -> Float, val maxSpeedFunc: (Boat) -> Double): GamePlugin() {

    companion object {
        var initialized = false
        fun hook() {
            if (!initialized) {
                initialized = true
                TransformManager.transformClass(SpeedyBoatTransformer())
            }
        }


    }

    override fun onEnable() {
        hook()
        eventManager.registerListener(this)
    }

    override fun onDisable() {

    }

    val strafeMap = HashMap<UUID, Float>()
    val forwardMap = HashMap<UUID, Float>()

    var Player.moveStrafing: Float
        get() = strafeMap[this.uniqueId]?:0.0f
        set(value) {
            strafeMap[this.uniqueId] = value
        }
    var Player.moveForward: Float
        get() = forwardMap[this.uniqueId]?:0.0f
        set(value) {
            forwardMap[this.uniqueId] = value
        }

    @Subscribe("speedyBoat-onPlayerQuit")
    fun onPlayerQuit(event: PlayerQuitGameEvent) {
        forwardMap.remove(event.player.uniqueId)
        strafeMap.remove(event.player.uniqueId)
    }

    @Subscribe("speedyBoat-onRightClickBoat")
    fun onRightClickBoat(event: PlayerInteractAtEntityEvent) {
        val boat = event.rightClicked
        if (event.player is Player && boat is Boat) {
            if (boat.passenger is ArmorStand) {
                if (boat.passenger.passenger == null) {
                    boat.passenger.passenger = event.player
                }
            }
            event.isCancelled = true
        }
    }
    @Subscribe("speedyBoat-onEnterVehicle")
    fun onEnterVehicle(event: VehicleEnterEvent) {
        val boat = event.vehicle
        if (event.entered is Player && boat is Boat) {
            if (boat.passenger is ArmorStand) {
                if (boat.passenger.passenger == null) {
                    boat.passenger.passenger = event.entered
                }
            }
            event.isCancelled = true
        }
    }
    @Subscribe("speedyBoat-onVehicleDamage")
    fun onVehicleDamage(event: VehicleDamageEvent) {
        val boat = event.vehicle
        if (boat.passenger is ArmorStand) {
            if (boat.passenger.passenger == event.attacker) {
                event.isCancelled = true
            }
        }
    }
    @Subscribe("speedyBoat-onVehicleDestroy", mustRunAfter = ["noBoatCrash-onVehicleDestroy"])
    fun onVehicleDestroy(event: VehicleDestroyEvent) {
        val boat = event.vehicle
        if (boat.passenger is ArmorStand) {
            if (boat.passenger.passenger == event.attacker) {
                event.isCancelled = true
                return
            }
            if (!event.isCancelled) {
                boat.passenger.remove()
            }
        }
    }
    val speedMultiplier = 0.07f


    @Subscribe("speedyBoat-onTick")
    fun onTick(event: EntityTickEvent) {
        val boat = event.entity
        if (boat is Boat) {
            if (boat.passenger !is ArmorStand) {
                boat.passenger?.leaveVehicle()
                val passenger = boat.world.spawn(boat.location, ArmorStand::class.java)
                passenger.isMarker = true
                passenger.isVisible = false
                boat.passenger = passenger
            }
            val player = (boat.passenger as ArmorStand).passenger?:return
            if (player !is Player) return
            val f: Float = player.location.yaw + -player.moveStrafing * 90.0f
            val x = -sin((f * Math.PI.toFloat() / 180.0f).toDouble()) * speedMultiplier * speedFunc(boat) * player.moveForward.toDouble() * 0.05000000074505806
            val z = cos((f * Math.PI.toFloat() / 180.0f).toDouble()) * speedMultiplier * speedFunc(boat) * player.moveForward.toDouble() * 0.05000000074505806
            boat.velocity = boat.velocity.add(Vector(x, 0.0, z))
            boatMaxSpeed[boat] = maxSpeedFunc(boat)

            if (player.moveForward == 0f && player.moveStrafing == 0f && instantStop) {
                boat.velocity = Vector(0, 0, 0)
            }
        }

    }

    @Subscribe("speedyBoat-onVehicleExit")
    fun onVehicleExit(event: VehicleExitEvent) {
        if (event.exited is Player) {
            if (event.vehicle is ArmorStand && event.vehicle.vehicle is Boat) {
                event.vehicle.vehicle.velocity = Vector(0, 0, 0)
            }
        }
        if (event.exited is ArmorStand) {
            if (event.vehicle is Boat) {
                event.isCancelled = true
            }
        }

    }

    @Subscribe("speedyBoat-onPlayerMove")
    fun onPlayerMove(event: PacketReceiveEvent) {
        if (event.original.packetType != PacketType.Play.Client.STEER_VEHICLE) {
            return
        }

        event.player.moveStrafing = event.original.packet.float.read(0)
        event.player.moveForward = event.original.packet.float.read(1)
        event.original.packet.float.write(0, 0f)
        event.original.packet.float.write(1, 0f)


    }


    class SpeedyBoatTransformer: MethodTransformer(EntityBoat::class.java.getDeclaredMethod("t_")) {
        override fun transform(methodNode: MethodNode) {
            val varManager = LocalVarManager(methodNode)

            val out = InsnList()
            for (instruction in methodNode.instructions) {
                if (instruction is LdcInsnNode) {
                    if (instruction.cst == 0.35) {
                        out.add(VarInsnNode(Opcodes.ALOAD, 0))
                        out.add(ASMUtils.reflectionMethodInvoke(PluginSpeedyBoatHook::getMaxSpeedCallback.javaMethod!!, varManager))
                        continue
                    }
                }
                out.add(instruction)
            }
            methodNode.instructions = out
        }

    }

}



object PluginSpeedyBoatHook {
    @JvmStatic
    fun getMaxSpeedCallback(entityBoat: EntityBoat): Double {
        return boatMaxSpeed[entityBoat.bukkitEntity]?:0.35
    }
}