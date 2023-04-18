package net.tilapiamc.communication

import java.util.*

// TODO: Implement server/list
data class ServerInfo(val host: String, val port: Int, val proxy: UUID, val serverId: UUID, val games: List<UUID>) {
}