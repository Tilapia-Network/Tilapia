package net.tilapiamc.communication

import java.util.*

data class ServerInfo(val proxy: UUID, val serverId: UUID, val games: List<UUID>) {
}