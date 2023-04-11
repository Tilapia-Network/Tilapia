package net.tilapiamc.gameextension.plugins

import com.comphenix.protocol.PacketType
import me.fan87.plugindevkit.events.ServerTickEvent
import net.minecraft.server.v1_8_R3.EntityBoat
import net.tilapiamc.api.events.annotation.Subscribe
import net.tilapiamc.api.events.game.PlayerJoinGameEvent
import net.tilapiamc.api.events.game.PlayerQuitGameEvent
import net.tilapiamc.api.events.packet.PacketReceiveEvent
import net.tilapiamc.api.hook.MethodTransformer
import net.tilapiamc.api.hook.TransformerManager
import net.tilapiamc.api.player.LocalNetworkPlayer
import net.tilapiamc.spigotcommon.game.plugin.GamePlugin
import org.bukkit.entity.Boat
import org.bukkit.entity.Player
import org.bukkit.event.vehicle.VehicleExitEvent
import org.bukkit.util.Vector
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodNode
import java.util.UUID
import kotlin.math.cos
import kotlin.math.sin

// This only works in 1.8
class PluginSpeedyBoat_1_8(val speedFunc: (Boat) -> Float): GamePlugin() {

    companion object {
        var initialized = false
        fun hook() {
            if (!initialized) {
                initialized = true
                TransformerManager.transformClass(SpeedyBoatTransformer())
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

    @Subscribe("speedyBoat-onPlayerJoin")
    fun onPlayerJoin(event: PlayerJoinGameEvent) {

    }
    val speedMultiplier = 0.07f

    @Subscribe("speedyBoat-onTick")
    fun onTick(event: ServerTickEvent) {
        val players = game.players.filterIsInstance<LocalNetworkPlayer>()
        for (player in players) {
            val boat = player.vehicle
            if (boat is Boat) {
                val f: Float = player.location.yaw + -player.moveStrafing * 90.0f
                val x = -sin((f * Math.PI.toFloat() / 180.0f).toDouble()) * speedMultiplier * speedFunc(boat) * player.moveForward.toDouble() * 0.05000000074505806
                val z = cos((f * Math.PI.toFloat() / 180.0f).toDouble()) * speedMultiplier * speedFunc(boat) * player.moveForward.toDouble() * 0.05000000074505806
                boat.velocity = boat.velocity.add(Vector(x, 0.0, z))
            }
        }

    }

    @Subscribe("speedyBoat-onVehicleExit")
    fun onVehicleExit(event: VehicleExitEvent) {
        if (event.exited is Player) {
            if (event.vehicle is Boat) {
                event.vehicle.velocity = Vector(0, 0, 0)
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

    class SpeedyBoatTransformer: MethodTransformer(EntityBoat::t_) {
        override fun transform(methodNode: MethodNode) {
            for (instruction in methodNode.instructions) {
                if (instruction is LdcInsnNode) {
                    if (instruction.cst == 0.35) {
                        println("Found 0.35")
                    }
                }
            }
        }

    }

}

