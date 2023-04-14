package net.tilapiamc.communication

import java.util.UUID

data class DatabaseLogin(val sessionId: UUID, val remoteIp: String, val username: String, val password: String)