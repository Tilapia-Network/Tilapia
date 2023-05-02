package net.tilapiamc.clientintegration

import net.tilapiamc.api.permission.PermissionManager

object ClientIntegrationPermissions {

    val DEBUG_MENU = PermissionManager.registerPermission("tilapia.debug_menu", true)

}