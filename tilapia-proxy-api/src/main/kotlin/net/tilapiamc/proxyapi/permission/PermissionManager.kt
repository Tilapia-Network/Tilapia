package net.tilapiamc.proxyapi.permission


object PermissionManager {

    val permissions = ArrayList<String>()

    init {

    }


    fun registerPermission(name: String): String {
        val permission = name
        permissions.add(permission)
        return permission
    }
    fun registerCommandPermission(commandName: String, action: String): String {
        return registerPermission("command.${commandName.lowercase().replace("_", "-")}${if (action.isEmpty()) "" else ".$action"}")
    }
    fun registerCommandUsePermission(commandName: String): String {
        return registerCommandPermission(commandName, "use")
    }

}

