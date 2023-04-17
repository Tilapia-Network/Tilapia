package net.tilapiamc.communication

import java.net.InetSocketAddress
import java.util.*

// TODO: Implement server/list
data class ServerInfo(val address: InetSocketAddress, val proxy: UUID, val serverId: UUID, val games: List<UUID>) {
}