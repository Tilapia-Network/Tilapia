package net.tilapiamc.proxyapi.command

import com.mojang.brigadier.CommandDispatcher
import com.velocitypowered.api.command.CommandSource

class ProxyCommandManager {

    val dispatcher = CommandDispatcher<CommandSource>()

}