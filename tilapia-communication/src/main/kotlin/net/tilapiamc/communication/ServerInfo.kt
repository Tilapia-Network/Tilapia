package net.tilapiamc.communication

import java.util.*

// TODO: Implement server/list
data class ServerInfo(val proxy: UUID, val serverId: UUID, val games: List<UUID>) {
}