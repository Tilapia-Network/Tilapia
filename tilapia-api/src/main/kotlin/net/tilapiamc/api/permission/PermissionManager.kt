package net.tilapiamc.api.permission

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault

object PermissionManager {

    val permissions = ArrayList<Permission>()

    init {

    }


    fun registerPermission(name: String, op: Boolean = true): Permission {
        val permission = Permission(name, if (op) PermissionDefault.OP else PermissionDefault.TRUE)
        permissions.add(permission)
        Bukkit.getServer().pluginManager.addPermission(permission)
        return permission
    }
    fun registerCommandPermission(commandName: String, action: String, op: Boolean = true): Permission {
        return registerPermission("command.${commandName.lowercase().replace("_", "-")}${if (action.isEmpty()) "" else ".$action"}", op)
    }
    fun registerCommandUsePermission(commandName: String, op: Boolean = true): Permission {
        return registerCommandPermission(commandName, "use", op)
    }

}

