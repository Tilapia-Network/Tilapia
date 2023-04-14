package net.tilapiamc.multiworld.args

import net.tilapiamc.command.ArgumentsContainer
import net.tilapiamc.command.CommandExecution
import net.tilapiamc.command.args.CommandArgument
import net.tilapiamc.multiworld.IllegalWorldNameException
import net.tilapiamc.multiworld.WorldManager
import net.tilapiamc.multiworld.WorldNotFoundException
import org.bukkit.Bukkit
import kotlin.reflect.KProperty

class WorldNameArgument<S>(name: String, val requireRegistered: Boolean, val requireLoaded: Boolean, val checkIllegal: Boolean = true, isRequired: Boolean = false): CommandArgument<String, S>(name, isRequired) {

    override fun getValue(any: Any?, property: KProperty<*>): CommandExecution<S>.() -> String? {
        return lambda@{
            val worldName = getArgString()?:return@lambda null
            if (requireRegistered) {
                if (!WorldManager.registeredWorlds.any { it.name == worldName }) {
                    throw WorldNotFoundException(worldName, true, false)
                }
            }
            if (requireLoaded) {
                if (!Bukkit.getWorlds().any { it.name == worldName }) {
                    throw WorldNotFoundException(worldName, false, true)
                }
            }
            if (checkIllegal) {
                if (!WorldManager.checkName(worldName)) {
                    throw IllegalWorldNameException(worldName)
                }
            }

            worldName
        }
    }

    override fun tabComplete(sender: S, token: String): Collection<String> {
        val out = ArrayList<String>()
        if (requireLoaded) {
            out.addAll(Bukkit.getWorlds().map { it.name })
        }
        if (requireRegistered) {
            out.addAll(WorldManager.registeredWorlds.map { it.name })
        }
        return out.filter { it.lowercase().startsWith(token) }
    }
}

fun <T> ArgumentsContainer<T>.worldNameArg(name: String, requireRegistered: Boolean, requireLoaded: Boolean, checkIllegal: Boolean = true, isRequired: Boolean = true): WorldNameArgument<T> {
    return addArgument(WorldNameArgument(name, requireRegistered, requireLoaded, checkIllegal, isRequired))
}
